package org.compiler;

import org.annotation.CheckNet;
import org.annotation.EchoEnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Description : BindViewProcessor 的辅助类
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/24
 * Email : 27674569@qq.com
 * Version : 1.0
 */
public class ProcessorHelper {
    // 生成辅助类的后辍名
    public static final String HELPER = "$_$ViewHelper";
    // 包名
    private String mPackageName;
    // 辅助类类名
    private String mHelperClassName;
    // 原clas的Element
    private TypeElement mClassElement;
    // ViewById 的 VariableElement集合
    public Map<Integer, VariableElement> viewByIdElements = new HashMap<>();
    // Event 的 ExecutableElement集合
    public Map<int[], ExecutableElement> eventElements = new HashMap<>();
    // Extra 的 VariableElement集合
    public Map<String, VariableElement> extraElements = new HashMap<>();
    // 辅助类对应的classname
    private String mOldClassName;


    public ProcessorHelper(TypeElement classElement, Elements elementUtils) {
        this.mClassElement = classElement;
        // 拿这个类型的包名的Element
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        // 获取package的全名称
        this.mPackageName = packageElement.getQualifiedName().toString();
        // 获取class名称
        String className = getClassName(classElement);
        mOldClassName = packageElement.getQualifiedName().toString() + "." + getClassName(classElement);
        // 辅助类的名称
        this.mHelperClassName = className + HELPER;
    }

    /**
     * 获取class名称
     */
    private String getClassName(TypeElement classElement) {
        // 截取位置不能含有包名，那么从包名的最后一位+1的位置开始截取
        int packageLenth = mPackageName.length() + 1;
        // 获取类名的全路径
        String classQualifiedName = classElement.getQualifiedName().toString();
        // 截取后的class名称
        String className = classQualifiedName.substring(packageLenth);
        return className.replace('.', '$');
    }

    /**
     * 获取生成辅助类的全类名 包名+类名
     */
    public String getHelperClassQualifiedName() {
        return mPackageName + "." + mHelperClassName;
    }

    /**
     * 返回当前helper对应class的ClassElement
     */
    public TypeElement getTypeElement() {
        return mClassElement;
    }

    /**
     * 生成的代码语句拼接
     */
    public String generateJavaCode() {
        StringBuilder sb = new StringBuilder();
        creatClassAndImplInject(sb);
        creatInitExtra(sb);
        creatInitView(sb);
        creatInitEvent(sb);
        // 类结束大括号
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * 创建类并实现V iewInject 接口
     */
    private void creatClassAndImplInject(StringBuilder sb) {
        String className = mClassElement.getQualifiedName().toString();
        sb.append("package ").append(mPackageName).append(";\n\n");
        sb.append("import android.view.View;\n");
        sb.append("import org.api.ViewFinder;\n");
        sb.append("import java.util.ArrayList;\n");
        sb.append("import org.api.ViewInject;\n\n");
        sb.append("public class ").append(mHelperClassName).append(" implements ViewInject<").append(className).append("> {\n\n");
        sb.append("    private ").append(className).append(" object;\n");
        sb.append("    private ViewFinder finder;\n\n");
        sb.append("    @Override\n");
        sb.append("    public void inject(").append(className).append(" object, ViewFinder finder) {\n");
        sb.append("        this.object = object;\n");
        sb.append("        this.finder = finder;\n");
        sb.append("        initExtra();\n");
        sb.append("        initView();\n");
        sb.append("        initEvent();\n");
        sb.append("    }\n\n");
    }

    /**
     * 创建Extra传参方法
     */
    private void creatInitExtra(StringBuilder sb) {
        sb.append("    private void initExtra() {\n");
        // 遍历 extraElements 集合
        for (String key : extraElements.keySet()) {
            // 根据key拿VariableElement
            VariableElement element = extraElements.get(key);
            // 拿属性名称
            String name = element.getSimpleName().toString();
            // 拿属性的类型(强转用)
            // String type = element.asType().toString();
            // 初始化变量 方法 activity.mName = finder.getExtra(key);
            sb.append("        object.").append(name).append(" = finder.getExtra(\"").append(key).append("\");\n");
        }
        sb.append("    }\n\n");
    }


    /**
     * 创建初始化控件
     */
    private void creatInitView(StringBuilder sb) {
        sb.append("    private void initView() {\n");
        // 遍历 iewByIdElements 集合
        for (Integer viewId : viewByIdElements.keySet()) {
            // 根据id拿VariableElement
            VariableElement element = viewByIdElements.get(viewId);
            // 拿属性名称
            String name = element.getSimpleName().toString();
            // 拿属性的类型(强转用)
            // String type = element.asType().toString();
            // 初始化变量 方法 activity.mTextView = finder.findViewById(viewId);
            sb.append("         object.").append(name).append(" = finder.findViewById(").append(viewId).append(");\n");
        }
        //  创建一个初始化点击事件的方法  换行此方法结束
        sb.append("    }\n\n");
    }

    /**
     * 创建点击事件
     */
    private void creatInitEvent(StringBuilder sb) {
        sb.append("    private void initEvent() {\n");
        // 如果没有点击事件
        if (eventElements.size() <= 0) {
            // 直接常见一个空方法即可
            sb.append("    }\n\n");
            return;
        }

        // id的集合
        sb.append("        ArrayList<Integer> ids = new ArrayList<>();\n");
        // 遍历 eventElements 的集合
        for (int[] viewIds : eventElements.keySet()) {
            // 首先清空集合
            sb.append("        ids.clear();\n");
            //  根据ids 拿到 VeventElements
            ExecutableElement element = eventElements.get(viewIds);
            // 拿到方法名
            String methodName = element.getSimpleName().toString();
            //  拿到方法需要穿的参数的 VariableElement --> void  setPreson（String name , int age）；
            // 这样拿到 两个参数的 VariableElement
            List<? extends VariableElement> parameters = element.getParameters();
            // 拿到所有的Id
            for (int viewId : viewIds) {
                // 添加id到集合
                sb.append("        ids.add(").append(viewId).append(");\n");
            }
            // 添加监听事件
            sb.append("        finder.setOnclickListenetByIds(ids, new ViewFinder.IFinderClickListener() {\n");
            sb.append("            @Override\n");
            sb.append("            public void onClick(View view) {\n");
            sb.append("                if (finder.onInterceptClickEvent(view,object,\"").append(methodName).append("\",");
            // 获取参数类型的全类名
            if (parameters != null && parameters.size() > 0) {
                sb.append("\"").append(parameters.get(0).asType().toString()).append("\"");
            } else {
                sb.append("null");
            }
            sb.append(")){\n");
            sb.append("                    return;\n");
            sb.append("                }\n");
            // 判断是否需要检测网络
            CheckNet checkNet = element.getAnnotation(CheckNet.class);
            if (checkNet != null) {
                sb.append("                if (!finder.isOpenNetWork()){\n");
                sb.append("                    return;\n");
                sb.append("                }\n");
            }

            // 判断是否静止使用重复点击
            EchoEnable echoEnable = element.getAnnotation(EchoEnable.class);
            if (echoEnable != null) {
                // 获取重复点击的时间
                long echoEnableTime = echoEnable.value();
                sb.append("                if (!finder.isFirstClick(").append(echoEnableTime).append(")){\n");
                sb.append("                    return;\n");
                sb.append("                }\n");
            }

            // 添加try catch
//            sb.append("                try {\n");
            // 开始回调方法
            sb.append("                    object.").append(methodName).append("(");
            // 判断是否需要强转
            if (parameters != null && parameters.size() > 0) {
                // 强转前括号
                sb.append("(");
                String typeName = parameters.get(0).asType().toString();
                sb.append(typeName).append(")view");
            }
            // 方法执行完毕后括号
            sb.append(");\n");
//            sb.append("                } catch (Exception e) {\n");
            // 抛出异常提示开发者
//            sb.append("                    throw new RuntimeException(e.getMessage());\n");
            // catch 结束括号
//            sb.append("                }\n");
            // onclick结束括号
            sb.append("            }\n");
            // finder回掉括号
            sb.append("        });\n");
        }
        sb.append("        ids = null;\n");
        // 方法结束大括号
        sb.append("    }\n\n");
    }


    /**
     * 创建unBind（）方法
     *
     * @param stringBuilder
     */
    /*private void creatUnBind(StringBuilder stringBuilder) {
        stringBuilder.append("    @Override\n")
                .append("    public void unBind() {\n")
                .append("        object = null;\n")
                .append("        finder = null;\n")
                .append("        viewIds = null;\n")
                .append("    }\n");
    }*/


}
