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
 * Description : BindViewProcessor �ĸ�����
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/24
 * Email : 27674569@qq.com
 * Version : 1.0
 */
public class ProcessorHelper {
    // ���ɸ�����ĺ����
    public static final String HELPER = "$_$ViewHelper";
    // ����
    private String mPackageName;
    // ����������
    private String mHelperClassName;
    // ԭclas��Element
    private TypeElement mClassElement;
    // ViewById �� VariableElement����
    public Map<Integer, VariableElement> viewByIdElements = new HashMap<>();
    // Event �� ExecutableElement����
    public Map<int[], ExecutableElement> eventElements = new HashMap<>();
    // Extra �� VariableElement����
    public Map<String, VariableElement> extraElements = new HashMap<>();
    // �������Ӧ��classname
    private String mOldClassName;


    public ProcessorHelper(TypeElement classElement, Elements elementUtils) {
        this.mClassElement = classElement;
        // ��������͵İ�����Element
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        // ��ȡpackage��ȫ����
        this.mPackageName = packageElement.getQualifiedName().toString();
        // ��ȡclass����
        String className = getClassName(classElement);
        mOldClassName = packageElement.getQualifiedName().toString() + "." + getClassName(classElement);
        // �����������
        this.mHelperClassName = className + HELPER;
    }

    /**
     * ��ȡclass����
     */
    private String getClassName(TypeElement classElement) {
        // ��ȡλ�ò��ܺ��а�������ô�Ӱ��������һλ+1��λ�ÿ�ʼ��ȡ
        int packageLenth = mPackageName.length() + 1;
        // ��ȡ������ȫ·��
        String classQualifiedName = classElement.getQualifiedName().toString();
        // ��ȡ���class����
        String className = classQualifiedName.substring(packageLenth);
        return className.replace('.', '$');
    }

    /**
     * ��ȡ���ɸ������ȫ���� ����+����
     */
    public String getHelperClassQualifiedName() {
        return mPackageName + "." + mHelperClassName;
    }

    /**
     * ���ص�ǰhelper��Ӧclass��ClassElement
     */
    public TypeElement getTypeElement() {
        return mClassElement;
    }

    /**
     * ���ɵĴ������ƴ��
     */
    public String generateJavaCode() {
        StringBuilder sb = new StringBuilder();
        creatClassAndImplInject(sb);
        creatInitExtra(sb);
        creatInitView(sb);
        creatInitEvent(sb);
        // �����������
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * �����ಢʵ��V iewInject �ӿ�
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
     * ����Extra���η���
     */
    private void creatInitExtra(StringBuilder sb) {
        sb.append("    private void initExtra() {\n");
        // ���� extraElements ����
        for (String key : extraElements.keySet()) {
            // ����key��VariableElement
            VariableElement element = extraElements.get(key);
            // ����������
            String name = element.getSimpleName().toString();
            // �����Ե�����(ǿת��)
            // String type = element.asType().toString();
            // ��ʼ������ ���� activity.mName = finder.getExtra(key);
            sb.append("        object.").append(name).append(" = finder.getExtra(\"").append(key).append("\");\n");
        }
        sb.append("    }\n\n");
    }


    /**
     * ������ʼ���ؼ�
     */
    private void creatInitView(StringBuilder sb) {
        sb.append("    private void initView() {\n");
        // ���� iewByIdElements ����
        for (Integer viewId : viewByIdElements.keySet()) {
            // ����id��VariableElement
            VariableElement element = viewByIdElements.get(viewId);
            // ����������
            String name = element.getSimpleName().toString();
            // �����Ե�����(ǿת��)
            // String type = element.asType().toString();
            // ��ʼ������ ���� activity.mTextView = finder.findViewById(viewId);
            sb.append("         object.").append(name).append(" = finder.findViewById(").append(viewId).append(");\n");
        }
        //  ����һ����ʼ������¼��ķ���  ���д˷�������
        sb.append("    }\n\n");
    }

    /**
     * ��������¼�
     */
    private void creatInitEvent(StringBuilder sb) {
        sb.append("    private void initEvent() {\n");
        // ���û�е���¼�
        if (eventElements.size() <= 0) {
            // ֱ�ӳ���һ���շ�������
            sb.append("    }\n\n");
            return;
        }

        // id�ļ���
        sb.append("        ArrayList<Integer> ids = new ArrayList<>();\n");
        // ���� eventElements �ļ���
        for (int[] viewIds : eventElements.keySet()) {
            // ������ռ���
            sb.append("        ids.clear();\n");
            //  ����ids �õ� VeventElements
            ExecutableElement element = eventElements.get(viewIds);
            // �õ�������
            String methodName = element.getSimpleName().toString();
            //  �õ�������Ҫ���Ĳ����� VariableElement --> void  setPreson��String name , int age����
            // �����õ� ���������� VariableElement
            List<? extends VariableElement> parameters = element.getParameters();
            // �õ����е�Id
            for (int viewId : viewIds) {
                // ���id������
                sb.append("        ids.add(").append(viewId).append(");\n");
            }
            // ��Ӽ����¼�
            sb.append("        finder.setOnclickListenetByIds(ids, new ViewFinder.IFinderClickListener() {\n");
            sb.append("            @Override\n");
            sb.append("            public void onClick(View view) {\n");
            sb.append("                if (finder.onInterceptClickEvent(view,object,\"").append(methodName).append("\",");
            // ��ȡ�������͵�ȫ����
            if (parameters != null && parameters.size() > 0) {
                sb.append("\"").append(parameters.get(0).asType().toString()).append("\"");
            } else {
                sb.append("null");
            }
            sb.append(")){\n");
            sb.append("                    return;\n");
            sb.append("                }\n");
            // �ж��Ƿ���Ҫ�������
            CheckNet checkNet = element.getAnnotation(CheckNet.class);
            if (checkNet != null) {
                sb.append("                if (!finder.isOpenNetWork()){\n");
                sb.append("                    return;\n");
                sb.append("                }\n");
            }

            // �ж��Ƿ�ֹʹ���ظ����
            EchoEnable echoEnable = element.getAnnotation(EchoEnable.class);
            if (echoEnable != null) {
                // ��ȡ�ظ������ʱ��
                long echoEnableTime = echoEnable.value();
                sb.append("                if (!finder.isFirstClick(").append(echoEnableTime).append(")){\n");
                sb.append("                    return;\n");
                sb.append("                }\n");
            }

            // ���try catch
//            sb.append("                try {\n");
            // ��ʼ�ص�����
            sb.append("                    object.").append(methodName).append("(");
            // �ж��Ƿ���Ҫǿת
            if (parameters != null && parameters.size() > 0) {
                // ǿתǰ����
                sb.append("(");
                String typeName = parameters.get(0).asType().toString();
                sb.append(typeName).append(")view");
            }
            // ����ִ����Ϻ�����
            sb.append(");\n");
//            sb.append("                } catch (Exception e) {\n");
            // �׳��쳣��ʾ������
//            sb.append("                    throw new RuntimeException(e.getMessage());\n");
            // catch ��������
//            sb.append("                }\n");
            // onclick��������
            sb.append("            }\n");
            // finder�ص�����
            sb.append("        });\n");
        }
        sb.append("        ids = null;\n");
        // ��������������
        sb.append("    }\n\n");
    }


    /**
     * ����unBind��������
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
