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
 * Description : ʵ���Լ����ļ�������
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/24
 * Email : 27674569@qq.com
 * Version : 1.0
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Elements mElements;
    // ����ʹ�ò�ͬ��ע�� �洢��ͬ��helper����
    private Map<String, ProcessorHelper> mHelpers = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        /**
         * ��ȡһ������Ԫ�أ������������߷����������¼����ӽӿڣ�
         * ExecutableElement����ʾĳ�����ӿڵķ��������췽�����ʼ�����򣨾�̬��ʵ����������ע������Ԫ�� ��
         * PackageElement����ʾһ��������Ԫ�أ�
         * TypeElement����ʾһ�����ӿڳ���Ԫ�أ�
         * TypeParameterElement����ʾһ���ࡢ�ӿڡ��������췽��Ԫ�ص���ʽ���Ͳ�����
         * VariableElement����ʾһ���ֶΡ�enum �������������췽���������ֲ��������쳣����
         */
        mElements = processingEnvironment.getElementUtils();
    }


    /**
     * �����������ע����ע�� SupportedSourceVersion������Դ����汾��
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        //���ص�ǰִ�л�����ȫ֧�ֵ����°汾
        return SourceVersion.latestSupported();
    }

    /**
     * ��Ӵ������ڱ�������Ҫ�����ע��
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
     * �������ڱ�����ɨ���ص��ĺ���
     *
     * @param roundEnvironment ���ڲ��ҳ�����Ԫ����ʹ�õ�ע��
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
     * ɨ��Extra
     */
    private void scanExtra(RoundEnvironment roundEnvironment) {
        Set<? extends Element> extraElements = roundEnvironment.getElementsAnnotatedWith(Extra.class);
        for (Element extraElement : extraElements) {
            // ����ǩ
            if (!checkAnnotation(extraElement, Extra.class, ElementKind.FIELD)) {
                return;
            }
            // �õ��ļ�Elenemt
            VariableElement fieldElement = (VariableElement) extraElement;
            // ����ȫ���� ��ȡ��Ӧ��helper
            ProcessorHelper helper = addHelperToHelpers(fieldElement);
            // �õ���ǰ���͵�ע�����
            Extra annotation = fieldElement.getAnnotation(Extra.class);
            // ��ȡע���е�ֵ
            String key = annotation.value();
            // ���浽��ǰhelper��ļ���
            helper.extraElements.put(key, fieldElement);
        }
    }

    /**
     * ɨ��ViewById
     */
    private void scanViewById(RoundEnvironment roundEnvironment) {
        Set<? extends Element> viewByIdElements = roundEnvironment.getElementsAnnotatedWith(ViewById.class);
        for (Element viewByIdElement : viewByIdElements) {
            // ����ǩ
            if (!checkAnnotation(viewByIdElement, ViewById.class, ElementKind.FIELD)) {
                return;
            }
            // �õ��ļ�Elenemt
            VariableElement fieldElement = (VariableElement) viewByIdElement;
            // ����ȫ���� ��ȡ��Ӧ��helper
            ProcessorHelper helper = addHelperToHelpers(fieldElement);
            // �õ���ǰ���͵�ע�����
            ViewById annotation = fieldElement.getAnnotation(ViewById.class);
            // ��ȡע���е�ֵ
            int id = annotation.value();
            // ���浽��ǰhelper��ļ���
            helper.viewByIdElements.put(id, fieldElement);
        }
    }

    /**
     * ɨ��Event
     */
    private void scanEvent(RoundEnvironment roundEnvironment) {
        Set<? extends Element> eventElements = roundEnvironment.getElementsAnnotatedWith(Event.class);
        for (Element eventElement : eventElements) {
            // ����ǩ
            if (!checkAnnotation(eventElement, Event.class, ElementKind.METHOD)) {
                return;
            }
            // �õ�����Elenemt
            ExecutableElement methodElement = (ExecutableElement) eventElement;
            // �õ����Element
            TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
            // ����ȫ���� ��ȡ��Ӧ��helper
            ProcessorHelper helper = addHelperToHelpers(methodElement);
            // �õ���ǰ���͵�ע�����
            Event annotation = methodElement.getAnnotation(Event.class);
            // ��ȡע���е�ֵ
            int[] ids = annotation.value();
            // ���浽��ǰhelper��ļ���
            helper.eventElements.put(ids, methodElement);
        }
    }

    /**
     * ���helper��helpers
     */
    private ProcessorHelper addHelperToHelpers(Element element) {
        // �õ����Element
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        // �õ���ǰȫ����
        String qualifileame = classElement.getQualifiedName().toString();
        // ����ȫ���� ��ȡ��Ӧ��helper
        ProcessorHelper helper = mHelpers.get(qualifileame);
        if (helper == null) {
            helper = new ProcessorHelper(classElement, mElements);
            mHelpers.put(qualifileame, helper);
        }
        return helper;
    }

    /**
     * ���Element�Ƿ���ȷ ����ElementKind �ж�
     */
    private boolean checkAnnotation(Element element, Class<?> typeClass, ElementKind elementKind) {
        // �ж��Ƿ���private��
        if (judgePrivate(element)) {
            error(element, "%s() must can not be private.", typeClass.getSimpleName());
            return false;
        }
        // �ж��Ƿ��Ƕ�Ӧ����
        if (elementKind != element.getKind()) {
            error(element, "%s must be declared on field.", element.getSimpleName());
            return false;
        }
        return true;
    }

    /**
     * ����ͬ���µ�viewhelper
     */
    private boolean creatClassByHelper() {
        // ��ʼ����java�ļ� ���������ж���Ҫ�������ٸ��ļ�
        for (String className : mHelpers.keySet()) {
            // �õ�helper ����������helperȡֵ��
            ProcessorHelper helper = mHelpers.get(className);

            try {
                // �����ֽ����ļ�
                JavaFileObject jfo = processingEnv.getFiler()
                        .createSourceFile(
                                helper.getHelperClassQualifiedName(),//ȫ����
                                helper.getTypeElement());//class��Element
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
     * error��־
     */
    private void error(Element element, String errorMsg, Object... args) {
        if (args.length > 0) {
            errorMsg = String.format(errorMsg, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, errorMsg, element);
    }

    /**
     * �ж��Ƿ���˽�е�
     */
    private boolean judgePrivate(Element element) {
        return element.getModifiers().contains(PRIVATE);
    }
}
