package cn.lbin.miaosha.constant;

public class MsgConstant {
    public static final String ATTR_NAME_EXCEPTION = "exception";
    public static final String MESSAGE_LOGIN_FAILED = "登录失败！请确认账号密码是否正确";
    public static final String MESSAGE_STRING_INVALIDATE = "字符串不能为空或长度为0！";
    public static final String MESSAGE_MOBILE_INVALIDATE = "手机号格式错误";
    public static final String MESSAGE_MOBILE_NOT_EXISTS = "手机号不存在";
    public static final String MESSAGE_SYSTEM_ERROR_LOGIN_NOT_UNIQUE = "错误！数据库中存在重复数据！";
    //登录的用户名
    public static final String LOGIN_ADMIN_NAME = "loginAdmin";
    public static final String REQUEST_ILLEGAL = "请求非法";
    public static final String ACCESS_LIMIT = "访问过于频繁";

    public static final String MESSAGE_ACCESS_FORBIDDEN = "还未登录，禁止访问受保护资源！";
    public static final String NAME_PAGE_INFO = "pageInfo";
    public static final String REDIS_CODE_PREFIX = "REDIS_CODE_PREFIX" ;
    public static final String ATTR_NAME_MESSAGE = "message";
    public static final String MESSAGE_CODE_NOT_EXIST = "验证码无效！请检查是否输入了正确的手机号";
    public static final String MESSAGE_CODE_INVALID = "验证码错误";
    public static final String ATTR_NAME_LOGIN_MEMBER = "loginMember";
    public static final String MESSAGE_HEADER_PIC_EMPTY = "头图不能为空！";
    public static final String MESSAGE_HEADER_PIC_UPLOAD_FAILED = "头图上传失败，请重试！";
    public static final String MESSAGE_DETAIL_PIC_EMPTY = "详情图片不能为空！";
    public static final String MESSAGE_DETAIL_PIC_UPLOAD_FAILED = "详情图片上传失败，请重试！";
    public static final String ATTR_NAME_TEMPLE_PROJECT = "templeProject";
    public static final String MESSAGE_RETURN_PIC_EMPTY = "上传回报图片不能为空！";
    public static final String MESSAGE_TEMPLE_PROJECT_MISSING = "临时ProjectVO对象未找到！";
    public static final String ATTR_NAME_PORTAL_TYPE_LIST = "portal_type_list";
    public static final String ATTR_NAME_DETAIL_PROJECT = "detailProjectVO";
    public static final String MIAOSHA_OVER="秒杀活动结束,库存不足";
    public static final String MIAOSHA_FAILED="秒杀活动结束,库存不足";
    public static final String MIAOSHA_REPEAT="秒杀活动商品只能购买一件";
    public static final String ORDER_NOT_EXIST="订单不存在";
}
