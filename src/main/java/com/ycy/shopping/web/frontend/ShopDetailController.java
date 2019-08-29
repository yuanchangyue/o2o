package com.ycy.shopping.web.frontend;

import com.ycy.shopping.dto.ProductExecution;
import com.ycy.shopping.entity.Product;
import com.ycy.shopping.entity.ProductCategory;
import com.ycy.shopping.entity.Shop;
import com.ycy.shopping.service.ProductCategoryService;
import com.ycy.shopping.service.ProductService;
import com.ycy.shopping.service.ShopService;
import com.ycy.shopping.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: o2o
 * @description: 商铺详情控制器
 * @author: YuanChangYue
 * @create: 2019-08-23 10:41
 */
@Controller
@RequestMapping("/frontend")
public class ShopDetailController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductService productService;

    /**
     * 获得商铺信息
     *
     * @param request 请求域 接收前端传递过来的值
     * @return 查询结果
     */
    @GetMapping("/shopdetailpageinfo")
    @ResponseBody
    public Map<String, Object> getShopDetail(HttpServletRequest request) {

        Map<String, Object> modelMap = new HashMap<>();

        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        Shop shop = null;
        List<ProductCategory> productCategoryList = null;

        if (shopId != -1) {

            shop = shopService.getByShopId(shopId);
            productCategoryList = productCategoryService.getProductCategoryList(shopId);

            modelMap.put("shop", shop);
            modelMap.put("productCategoryList", productCategoryList);
            modelMap.put("success", true);

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "the shopId is empty!");
        }
        return modelMap;
    }


    /**
     * 查询该商铺下面的全部商品
     *
     * @param request 请求域 接收前端传递过来的值
     * @return 查询结果
     */
    @GetMapping("getproductbyshop")
    @ResponseBody
    public Map<String, Object> getProductByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();

        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");

        if ((shopId > -1) && (pageIndex > -1) && (pageSize > -1)) {

            String productName = HttpServletRequestUtil.getString(request, "productName");
            long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
            Product product = compactProductCondition(shopId, productName, productCategoryId);

            ProductExecution pe = productService.getProductList(product, pageIndex, pageSize);
            modelMap.put("productList", pe.getProductList());
            modelMap.put("count", pe.getCount());
            modelMap.put("success", true);

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "shopId or pageIndex , pageSize is empty ");
        }
        return modelMap;
    }

    /**
     * 将查询product的多个条件进行合并
     *
     * @param shopId            商铺id
     * @param productName       商品名称
     * @param productCategoryId 商品类别id
     * @return 携带对各条件的商品类
     */
    private Product compactProductCondition(long shopId, String productName, long productCategoryId) {
        Product conditionProduct = new Product();
        Shop shop = new Shop();
        shop.setShopId(shopId);
        conditionProduct.setShop(shop);

        if (!StringUtils.isEmpty(productName)) {
            conditionProduct.setProductName(productName);
        }
        if (productCategoryId != -1L) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            conditionProduct.setProductCategory(productCategory);
        }
        conditionProduct.setEnableStatus(1);
        return conditionProduct;
    }


}
