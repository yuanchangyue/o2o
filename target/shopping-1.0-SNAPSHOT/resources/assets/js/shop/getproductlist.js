$(function () {

    var pageIndex = 1;
    var pageSize = 990;
    var shopId = getQueryString("shopId");
    var productPostUrl = 'http://localhost:8080/shopping/shopadmin/modifyproduct';
    var productInfo = "http://localhost:8080/shopping/shopadmin/getproductlistbyshop";
    var saveCurrentShop = 'http://localhost:8080/shopping/shopadmin/getshopmanagementinfo';


    transferCurrentShop(shopId);

    /**
     * 将需要修改的shopId传递到后台
     * @param shopId
     */
    function transferCurrentShop(shopId) {
        $.ajax({
            url: saveCurrentShop,
            type: 'POST',
            xhrFields: {
                withCredentials: true
            },
            crossDomain: true,
            data: {
                shopId: shopId
            },
            success: function (data) {
                console.info("传递成功！");
            }
        });
    }

    getAndShowProductList();

    /**
     * 获得并展示商品列表
     */
    function getAndShowProductList() {

        var getProductListInfoUrl = productInfo + "?pageIndex=" + pageIndex + "&pageSize=" + pageSize;

        console.info(getProductListInfoUrl);

        $.getJSON(getProductListInfoUrl, function (data) {

            console.info(data);
            var productsHtml = "";

            $.map(data.productList, function (value, index) {

                var textOp = "下架";
                var status = 0;
                if (value.enableStatus === 0) {
                    textOp = "上架";
                    status = 1;
                } else {
                    status = 0;
                }
                productsHtml += '<tr>'
                    + '<th scope="row">' + (index + 1) + '</th>'
                    + '<td>' + value.productName + '</td>'
                    + '<td>' + value.priority + '</td>'
                    + '<td>' +
                    '<a href="#" class="btn btn-primary btn-sm product-edit-btn" data-toggle="modal" data-target="#product-edit" data-id="' + value.productId + '">编辑</a>&nbsp;' +
                    '<a href="" class="btn btn-danger btn-sm product-status-btn" data-status="' + status + '" data-id="' + value.productId + '">' + textOp + '</a>&nbsp;' +
                    '<a href="" class="btn btn-info btn-sm">预览</a>&nbsp;' + '</td>'
                    + '</tr>';
            });

            $(".product-table-body").html(productsHtml);

        });
    }

    /**
     * 监听商品上下架功能
     */
    $(document).on("click", ".product-status-btn", function () {
        var status = $(this).data("status");
        var id = $(this).data("id");

        var product = {};
        product.productId = id;
        product.enableStatus = status;

        if (confirm("确定吗?")) {
            $.ajax({
                url: productPostUrl,
                type: "POST",
                data: {
                    productStr: JSON.stringify(product),
                    statusChange: true
                },
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        alert("操作成功");
                        getAndShowProductList();
                    } else {
                        alert("操作失败");
                    }
                }
            });
        }
    });

    $(document).on("click", ".product-edit-btn", function () {
        window.location.href = "/shopping/shopadmin/productoperation?productId=" + $(this).data("id");
    });

    $("#btn-add-product").click(function () {
        window.location.href = "/shopping/shopadmin/productoperation";
    });

});