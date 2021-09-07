# Swagger V3 (OpenAPI) 文档生成及查看器

## 资源链接

* 演示地址 [https://demo.budwk.com/swagger](https://demo.budwk.com/swagger)
* 前端源码 [https://github.com/budwk/budwk-openapi-viewer](https://github.com/budwk/budwk-openapi-viewer)
* 欢迎提交代码,完善本工具(含前端)

## 特点说明

* 兼容nutz MVC 注解,如 `@At @Ok @ApiVersion @GET @POST @DELETE` 自动生成V3文档
* 表单传参使用 `@ApiFormParams` 定义,支持POJO类映射和单参数定义
* 路径参数或查询参数在 `@Operation` 内使用 `@Parameter` 定义,如 `in = ParameterIn.PATH` `in = ParameterIn.QUERY`
* 路径参数使用 `{}` 定义,以便兼容 nutz 和本工具包,如 `@At("/get/{id}")`
* 更多用法请查阅 Swagger3 文档 [https://swagger.io/specification](https://swagger.io/specification)

## 完整示例
* 配置文件
```text
swagger.enable=true
swagger.scanner.package=com.budwk.nb.web.controllers
swagger.info.title=BudWk V6 API
swagger.info.version=1.0.0
swagger.info.contact.name=大鲨鱼
swagger.info.contact.email=wizzer@qq.com
```
* Java代码
```java

@IocBean
@At("/api/{version}/platform/shop/config/take")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "商城_商城配置_自提点管理")}, servers = {@Server(url = "/")})
public class ShopConfigTakeController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private ShopConfigTakeService shopConfigTakeService;

    @Inject
    @Reference(check = false)
    private ShopConfigAreaService shopConfigAreaService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresAuthentication
    @Operation(
            tags = "商城_商城配置_自提点管理", summary = "分页查询",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(shopConfigTakeService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("shop.config.take.create")
    @SLog(tag = "新增自提点", msg = "自提点名称:${shopConfigTake.name}")
    @Operation(
            tags = "商城_商城配置_自提点管理", summary = "新增自提点",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "shop.config.take.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Shop_config_take.class
    )
    public Object create(@Param("..") Shop_config_take shopConfigTake, HttpServletRequest req) {
        try {
            shopConfigTake.setAreaName(shopConfigAreaService.getFullName(shopConfigTake.getAreaCode()));
            shopConfigTake.setCreatedBy(StringUtil.getPlatformUid());
            shopConfigTake.setUpdatedBy(StringUtil.getPlatformUid());
            shopConfigTakeService.insert(shopConfigTake);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("shop.config.take.update")
    @SLog(tag = "修改自提点", msg = "自提点名称:${shopConfigTake.name}")
    @Operation(
            tags = "商城_商城配置_自提点管理", summary = "修改自提点",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "shop.config.take.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Shop_config_take.class
    )
    public Object update(@Param("..") Shop_config_take shopConfigTake, HttpServletRequest req) {
        try {
            shopConfigTake.setAreaName(shopConfigAreaService.getFullName(shopConfigTake.getAreaCode()));
            shopConfigTake.setUpdatedBy(StringUtil.getPlatformUid());
            shopConfigTakeService.updateIgnoreNull(shopConfigTake);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @RequiresAuthentication
    @Operation(
            tags = "商城_商城配置_自提点管理", summary = "获取自提点",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object get(String id, HttpServletRequest req) {
        try {
            return Result.success().addData(shopConfigTakeService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("shop.config.take.delete")
    @SLog(tag = "删除自提点")
    @Operation(
            tags = "商城_商城配置_自提点管理", summary = "删除自提点",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "shop.config.take.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object delete(String id, HttpServletRequest req) {
        try {
            Shop_config_take shopConfigTake = shopConfigTakeService.fetch(id);
            if (shopConfigTake == null) {
                return Result.error("system.error.noData");
            }
            shopConfigTakeService.delete(id);
            req.setAttribute("_slog_msg", String.format("删除自提点名称:%s", shopConfigTake.getId()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
```