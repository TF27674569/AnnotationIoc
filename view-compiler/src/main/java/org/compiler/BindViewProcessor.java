package org.compiler;

import com.google.auto.service.AutoService;

import org.annotation.CheckNet;
import org.annotation.Event;
import org.annotation.Extra;
import org.annotation.ViewById;

import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.Modifier.PRIVATE;

/**
 * Description : 实现自己码文件的生成
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/24
 * Email : 27674569@qq.com
 * Version : 1.0
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Elements mElements;
    // 根据使用不同的注解 存储不同的helper对象
    private Map<String, ProcessorHelper> mHelpers = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        /**
         * 获取一个程序元素，比如包、类或者方法，有如下几种子接口：
         * ExecutableElement：表示某个类或接口的方法、构造方法或初始化程序（静态或实例），包括注解类型元素 ；
         * PackageElement：表示一个包程序元素；
         * TypeElement：表示一个类或接口程序元素；
         * TypeParameterElement：表示一般类、接口、方法或构造方法元素的形式类型参数；
         * VariableElement：表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数
         */
        mElements = processingEnvironment.getElementUtils();
    }


    /**
     * 如果处理器类注释在注释 SupportedSourceVersion，返回源代码版本。
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        //返回当前执行环境完全支持的最新版本
        return SourceVersion.latestSupported();
    }

    /**
     * 添加处理器在编译期需要处理的注解
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annottoanTypes = new LinkedHashSet<>();
        annottoanTypes.add(ViewById.class.getCanonicalName());
        annottoanTypes.add(Event.class.getCanonicalName());
        annottoanTypes.add(Extra.class.getCanonicalName());
        annottoanTypes.add(CheckNet.class.getCanonicalName());
        return annottoanTypes;
    }

    /**
     * 编译器在编译期扫描后回调的函数
     *
     * @param roundEnvironment 用于查找出程序元素上使用的注解
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mHelpers.clear();
        scanExtra(roundEnvironment);
        scanViewById(roundEnvironment);
        scanEvent(roundEnvironment);
        return creatClassByHelper();
    }

    /**
     * 扫描Extra
     */
    private void scanExtra(RoundEnvironment roundEnvironment) {
        Set<? extends Element> extraElements = roundEnvironment.getElementsAnnotatedWith(Extra.class);
        for (Element extraElement : extraElements) {
            // 检测标签
            if (!checkAnnotation(extraElement, Extra.class, ElementKind.FIELD)) {
                return;
            }
            // 拿到文件Elenemt
            VariableElement fieldElement = (VariableElement) extraElement;
            // 根据全类名 获取对应的helper
            ProcessorHelper helper = addHelperToHelpers(fieldElement);
            // 拿到当前类型的注解对象
            Extra annotation = fieldElement.getAnnotation(Extra.class);
            // 获取注解中的值
            String key = annotation.value();
            // 保存到当前helper类的集合
            helper.extraElements.put(key, fieldElement);
        }
    }

    /**
     * 扫描ViewById
     */
    private void scanViewById(RoundEnvironment roundEnvironment) {
        Set<? extends Element> viewByIdElements = roundEnvironment.getElementsAnnotatedWith(ViewById.class);
        for (Element viewByIdElement : viewByIdElements) {
            // 检测标签
            if (!checkAnnotation(viewByIdElement, ViewById.class, ElementKind.FIELD)) {
                return;
            }
            // 拿到文件Elenemt
            VariableElement fieldElement = (VariableElement) viewByIdElement;
            // 根据全类名 获取对应的helper
            ProcessorHelper helper = addHelperToHelpers(fieldElement);
            // 拿到当前类型的注解对象
            ViewById annotation = fieldElement.getAnnotation(ViewById.class);
            // 获取注解中的值
            int id = annotation.value();
            // 保存到当前helper类的集合
            helper.viewByIdElements.put(id, fieldElement);
        }
    }

    /**
     * 扫描Event
     */
    private void scanEvent(RoundEnvironment roundEnvironment) {
        Set<? extends Element> eventElements = roundEnvironment.getElementsAnnotatedWith(Event.class);
        for (Element eventElement : eventElements) {
            // 检测标签
            if (!checkAnnotation(eventElement, Event.class, ElementKind.METHOD)) {
                return;
            }
            // 拿到方法Elenemt
            ExecutableElement methodElement = (ExecutableElement) eventElement;
            // 拿到类的Element
            TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
            // 根据全类名 获取对应的helper
            ProcessorHelper helper = addHelperToHelpers(methodElement);
            // 拿到当前类型的注解对象
            Event annotation = methodElement.getAnnotation(Event.class);
            // 获取注解中的值
            int[] ids = annotation.value();
            // 保存到当前helper类的集合
            helper.eventElements.put(ids, methodElement);
        }
    }

    /**
     * 添加helper到helpers
     */
    private ProcessorHelper addHelperToHelpers(Element element) {
        // 拿到类的Element
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        // 拿到当前全类名
        String qualifileame = classElement.getQualifiedName().toString();
        // 根据全类名 获取对应的helper
        ProcessorHelper helper = mHelpers.get(qualifileame);
        if (helper == null) {
            helper = new ProcessorHelper(classElement, mElements);
            mHelpers.put(qualifileame, helper);
        }
        return helper;
    }

    /**
     * 检测Element是否正确 根据ElementKind 判断
     */
    private boolean checkAnnotation(Element element, Class<?> typeClass, ElementKind elementKind) {
        // 判断是否是private的
        if (judgePrivate(element)) {
            error(element, "%s() must can not be private.", typeClass.getSimpleName());
            return false;
        }
        // 判断是否是对应类型
        if (elementKind != element.getKind()) {
            error(element, "%s must be declared on field.", element.getSimpleName());
            return false;
        }
        return true;
    }

    /**
     * 创建同包下的viewhelper
     */
    private boolean creatClassByHelper() {
        // 开始创建java文件 根据类名判断需要创建多少个文件
        for (String className : mHelpers.keySet()) {
            // 拿到helper （后续根据helper取值）
            ProcessorHelper helper = mHelpers.get(className);

            try {
                // 创建字节码文件
                JavaFileObject jfo = processingEnv.getFiler()
                        .createSourceFile(
                                helper.getHelperClassQualifiedName(),//全类名
                                helper.getTypeElement());//class的Element
                Writer writer = jfo.openWriter();
                writer.write(helper.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
                error(helper.getTypeElement(),
                        "Unable to write injector for type %s: %s",
                        helper.getTypeElement(), e.getMessage());
                return false;
            }
        }
        return true;
    }


    /**
     * error日志
     */
    private void error(Element element, String errorMsg, Object... args) {
        if (args.length > 0) {
            errorMsg = String.format(errorMsg, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, errorMsg, element);
    }

    /**
     * 判断是否是私有的
     */
    private boolean judgePrivate(Element element) {
        return element.getModifiers().contains(PRIVATE);
    }
}
