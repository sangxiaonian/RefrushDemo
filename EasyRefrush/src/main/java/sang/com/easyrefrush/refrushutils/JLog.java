package sang.com.easyrefrush.refrushutils;

import android.util.Log;

public class JLog {
    /**
     * tag
     */
    public static String tag = "PING";


    public static void i(String content) {
        JLog.i(tag, getLogInfo() + content);
    }

    public static void i(String tag, String content) {
        Log.i(tag, getLogInfo() + content);
    }

    public static void d(String content) {
        JLog.d(tag, getLogInfo() + content);
    }

    public static void d(String tag, String content) {
        Log.d(tag, getLogInfo() + content);
    }

    public static void e(String content) {
        JLog.e(tag, getLogInfo() + content);
    }

    public static void e(String tag, String content) {

        Log.e(tag, getLogInfo() + content);
    }

    public static void v(String content) {
        JLog.v(tag, getLogInfo() + content);
    }

    public static void v(String tag, String content) {
        Log.v(tag, getLogInfo() + content);
    }

    public static void w(String content) {
        JLog.w(tag, getLogInfo() + content);
    }

    public static void w(String tag, String content) {
        Log.w(tag, getLogInfo() + content);
    }

    private static String getLogInfo() {

        StackTraceElement targetStackTracelement = getTargetStackTraceElement();

        return "Thread:" + Thread.currentThread().getName() + "\n" +
                getClassName() + "." + getMethodName() + "\t("
                + targetStackTracelement.getFileName() + ":"
                + targetStackTracelement.getLineNumber() + ")\n";
    }


    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(JLog.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }

    /**
     * 获取类名
     *
     * @return
     */
    private static String getClassName() {
        try {
            String classPath = Thread.currentThread().getStackTrace()[5].getClassName();
            return classPath.substring(classPath.lastIndexOf(".") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取方法名
     *
     * @return
     */
    private static String getMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[5].getMethodName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 打印log的行号
     * 
     *
     * @return
     */
    private static int getLineNumber() {
        try {
            return Thread.currentThread().getStackTrace()[5].getLineNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


}