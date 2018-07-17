package sang.com.easyrefrush.refrush;

/**
 * 作者： ${PING} on 2018/7/11.
 */

public class EnumCollections {

    /**
     * 用来枚举滑动方向，UP向上滑动，Down 向下滑动
     */
    public  enum Direction{
        UP,Down;
    }


    /**
     * 默认头部类型
     */
    public enum HeadStyle{
        /**
         * 视差特效
         */
        PARALLAX,
        /**
         * 刷新
         */
        REFRUSH
    }

    /**
     * 用来枚举布局为头布局还是脚布局，UP ，Down
     */
    public  enum Loaction{
        UP,Down;
    }

}
