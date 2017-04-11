package com.smartserver.compiler;

import com.google.auto.service.AutoService;
import com.smartserver.annotation.Controller;
import com.smartserver.annotation.RequestMapping;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class MappingProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtil;
    private List<RuleParser> parses;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
        messager = env.getMessager();
        elementUtil = env.getElementUtils();
        parses = new ArrayList<>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(Controller.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
        collectControllerInfo(env);
        generateRuleRegistry();
        return true;
    }

    private void collectControllerInfo(RoundEnvironment env) {
        Set<? extends Element> controllerElements = env.getElementsAnnotatedWith(Controller.class);
        for (Element controllerElement : controllerElements) {
            if (controllerElement.getKind() == ElementKind.CLASS) {
                List<? extends Element> memberElements = elementUtil.getAllMembers((TypeElement) controllerElement);
                for (Element actionElement : memberElements) {
                    RequestMapping mapping = actionElement.getAnnotation(RequestMapping.class);
                    if (mapping != null) {
                        parses.add(new RuleParser.Builder((TypeElement) controllerElement,
                                (ExecutableElement) actionElement).build());
                    }
                }
            }
        }
    }

    private void generateRuleRegistry() {
        log("start generate ruleRegistry.");
        // List rules
        FieldSpec rules = FieldSpec.builder(List.class, "rules")
                .addModifiers(Modifier.PRIVATE)
                .build();

        // init()
        MethodSpec.Builder initBuilder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PRIVATE);
        log("parses size:%s", parses.size());
        for (int i = 0; i < parses.size(); i++) {
            initBuilder.addStatement("rules.add($S)", parses.get(i).parse());
        }
        MethodSpec init = initBuilder.build();

        // 构造方法
        ClassName arrayList = ClassName.get(ArrayList.class);
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N = new $T<String>()", rules, arrayList)
                .addStatement("$N()", init)
                .build();

        // getRules()
        MethodSpec getRules = MethodSpec.methodBuilder("getRules")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return rules")
                .returns(List.class)
                .build();

        // 开始组装类
        TypeSpec type = TypeSpec.classBuilder("RuleRegistry")
                .addModifiers(Modifier.PUBLIC)
                .addField(rules)
                .addMethod(constructor)
                .addMethod(init)
                .addMethod(getRules)
                .build();

        try {
            JavaFile.builder("com.smartserver.generated", type)
                    .build()
                    .writeTo(filer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void log(String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(message, args));
    }

    private void error(String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args));
    }
}
