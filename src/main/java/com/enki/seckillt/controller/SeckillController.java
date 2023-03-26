package com.enki.seckillt.controller;


import com.enki.seckillt.annotations.AccessLimit;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.common.Const;
import com.enki.seckillt.entity.OrderInfo;
import com.enki.seckillt.entity.SeckillOrder;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.mq.MQSender;
import com.enki.seckillt.mq.SeckillMessage;
import com.enki.seckillt.redis.GoodsKey;
import com.enki.seckillt.redis.RedisService;
import com.enki.seckillt.redis.UserKey;
import com.enki.seckillt.result.CodeMsg;
import com.enki.seckillt.result.Result;
import com.enki.seckillt.service.GoodsService;
import com.enki.seckillt.service.SeckillGoodsService;
import com.enki.seckillt.service.SeckillOrderService;
import com.enki.seckillt.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @author Enki
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping("seckill")
public class SeckillController implements InitializingBean {


    @Resource
    private RedisService redisService;

    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private GoodsService goodsService;

    @Resource
    private SeckillOrderService seckillOrderService;

    @Resource
    private MQSender mqSender;

    /**
     * 如果是集群情况下，需要达到一定量此缓存才能起到重大作用
     */
    private final HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 将库存初始化到本地缓存及redis缓存，原则上次块应该在创建秒杀活动时候触发的（为了演示，此项目没有创建活动逻辑，所有放在启动项目时候放进内存）
     */
    public void afterPropertiesSet() throws Exception {
        log.info("我在启动。。。。。。。。。");
//        初始化商品信息
        List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
        if (goodsList == null) {
            return;
        }
        for (GoodsBo goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, String.valueOf(goods.getId()), goods.getStockCount(), Const.RedisCacheExtime.GOODS_LIST);
            localOverMap.put(goods.getId(), false);
        }
    }

    @RequestMapping("/seckill2")
    public String list2(Model model,
                        @RequestParam("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }
        //判断库存
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillOrderService.insert(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }

    /**
     * 秒杀
     *
     * @param model
     * @param goodsId 路径中的变量
     * @param path    请求中的变量
     * @param request
     * @return
     */
    @PostMapping("/{path}/seckill")
//    @ResponseBody
    public Result<Integer> list(
            @RequestParam("goodsId") long goodsId,
            @PathVariable("path") String path,
            HttpServletRequest request) {

        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        //验证path
        boolean check = seckillOrderService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, String.valueOf(goodsId));
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(mm);
        return Result.success(0);
    }

    /**
     * 客户端轮询查询是否下单成功
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(@RequestParam("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        long result = seckillOrderService.getSeckillResult((long) user.getId(), goodsId);
        return Result.success(result);
    }

    /**
     * 秒杀路径创建 同时会将路径传给前端
     *
     * @param request
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @GetMapping("/path")
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, User user,
                                         @RequestParam("goodsId") long goodsId) {
        String loginToken = CookieUtil.readLoginToken(request);
        user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        String path = seckillOrderService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }
}
