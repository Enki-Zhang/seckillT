package com.enki.seckillt.controller;


import cn.hutool.core.util.StrUtil;
import com.enki.seckillt.bo.GoodsBo;
import com.enki.seckillt.common.Const;
import com.enki.seckillt.entity.User;
import com.enki.seckillt.redis.GoodsKey;
import com.enki.seckillt.redis.RedisService;
import com.enki.seckillt.redis.UserKey;
import com.enki.seckillt.result.CodeMsg;
import com.enki.seckillt.result.Result;
import com.enki.seckillt.service.SeckillGoodsService;
import com.enki.seckillt.util.CookieUtil;
import com.enki.seckillt.vo.GoodsDetailVo;
import org.springframework.context.ApplicationContext;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Enki
 * @Version 1.0
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private RedisService redisService;
    @Resource
    private SeckillGoodsService seckillGoodsService;


    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;


    @RequestMapping("/list")
    public String list(Model model, HttpServletRequest request, HttpServletResponse response) {
        // 缓存查询页面信息
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StrUtil.isBlank(html)) {
            return html;
        }
//        查询秒杀商品清单
        List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
//        向view中添加数据
        model.addAttribute("goodsList", goodsList);
        IWebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StrUtil.isBlank(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html, Const.RedisCacheExtime.GOODS_LIST);
        }
        return html;
    }

    @RequestMapping("/to_detail2/{goodsId}")
    @ResponseBody
    public String detail2(Model model,
                          @PathVariable("goodsId") long goodsId, HttpServletRequest request, HttpServletResponse response) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        model.addAttribute("user", user);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        if (goods == null) {
            return "没有找到该页面";
        } else {
            model.addAttribute("goods", goods);
            long startAt = goods.getStartDate().getTime();
            long endAt = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();

            int miaoshaStatus = 0;
            int remainSeconds = 0;
            if (now < startAt) {//秒杀还没开始，倒计时
                remainSeconds = (int) ((startAt - now) / 1000);
            } else if (now > endAt) {//秒杀已经结束
                miaoshaStatus = 2;
                remainSeconds = -1;
            } else {//秒杀进行中
                miaoshaStatus = 1;
            }
            model.addAttribute("seckillStatus", miaoshaStatus);
            model.addAttribute("remainSeconds", remainSeconds);
            IWebContext ctx = new WebContext(request, response,
                    request.getServletContext(), request.getLocale(), model.asMap());
            html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
            if (!StringUtils.isEmpty(html)) {
                redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html, Const.RedisCacheExtime.GOODS_INFO);
            }
            return html;
        }
    }

    /**
     * 秒杀详情页
     *
     * @param model
     * @param goodsId
     * @param request
     * @return
     */
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(Model model,
                                        @PathVariable("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        String sessionId = request.getSession().getId();


//        查询redis中的token
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
//根据id查询秒杀商品
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        if (goods == null) {
//            商品不存在
            return Result.error(CodeMsg.NO_GOODS);
        } else {
            if (goods.getStockCount() < 1) {
                return Result.error(CodeMsg.INSUFFICIENT_INVENTORY);
            }
            model.addAttribute("goods", goods);
            long startAt = goods.getStartDate().getTime();
            long endAt = goods.getEndDate().getTime();
            long now = System.currentTimeMillis();

            int miaoshaStatus = 0;
            int remainSeconds = 0;
            if (now < startAt) {//秒杀还没开始，倒计时
                remainSeconds = (int) ((startAt - now) / 1000);
            } else if (now > endAt) {//秒杀已经结束
                miaoshaStatus = 2;
                remainSeconds = -1;
            } else {//秒杀进行中
                miaoshaStatus = 1;
            }
//            包装商品信息
            GoodsDetailVo vo = new GoodsDetailVo();
            vo.setGoods(goods);
            vo.setUser(user);
            vo.setRemainSeconds(remainSeconds);
            vo.setMiaoshaStatus(miaoshaStatus);
            return Result.success(vo);
        }
    }
}

