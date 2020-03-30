package org.nutz.boot.starter.swagger3;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.core.util.ParameterProcessor;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.nutz.mvc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/2/18
 */
public class NutzReaderUtils {
    // 以下已实现
    private static final String GET_METHOD = "get";
    private static final String POST_METHOD = "post";
    private static final String PUT_METHOD = "put";
    private static final String DELETE_METHOD = "delete";
    // 以下未实现
    private static final String HEAD_METHOD = "head";
    private static final String OPTIONS_METHOD = "options";

    private static final String PATH_DELIMITER = "/";

    /**
     * Collects constructor-level parameters from class.
     *
     * @param cls        is a class for collecting
     * @param components
     * @return the collection of supported parameters
     */
    public static List<Parameter> collectConstructorParameters(Class<?> cls, Components components, javax.ws.rs.Consumes classConsumes, JsonView jsonViewAnnotation) {
        if (cls.isLocalClass() || (cls.isMemberClass() && !Modifier.isStatic(cls.getModifiers()))) {
            return Collections.emptyList();
        }

        List<Parameter> selected = Collections.emptyList();
        int maxParamsCount = 0;

        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            if (!ReflectionUtils.isConstructorCompatible(constructor)
                    && !ReflectionUtils.isInject(Arrays.asList(constructor.getDeclaredAnnotations()))) {
                continue;
            }

            final Type[] genericParameterTypes = constructor.getGenericParameterTypes();
            final Annotation[][] annotations = constructor.getParameterAnnotations();

            int paramsCount = 0;
            final List<Parameter> parameters = new ArrayList<Parameter>();
            for (int i = 0; i < genericParameterTypes.length; i++) {
                final List<Annotation> tmpAnnotations = Arrays.asList(annotations[i]);

                final Type genericParameterType = genericParameterTypes[i];
                final List<Parameter> tmpParameters = collectParameters(genericParameterType, tmpAnnotations, components, classConsumes, jsonViewAnnotation);
                if (tmpParameters.size() >= 1) {
                    for (Parameter tmpParameter : tmpParameters) {
                        Parameter processedParameter = ParameterProcessor.applyAnnotations(
                                tmpParameter,
                                genericParameterType,
                                tmpAnnotations,
                                components,
                                new String[0],
                                null,
                                null);
                        if (processedParameter != null) {
                            parameters.add(processedParameter);
                        }
                    }
                    paramsCount++;
                }
            }

            if (paramsCount >= maxParamsCount) {
                maxParamsCount = paramsCount;
                selected = parameters;
            }
        }

        return selected;
    }

    private static List<Parameter> collectParameters(Type type, List<Annotation> annotations, Components components, javax.ws.rs.Consumes classConsumes, JsonView jsonViewAnnotation) {
        final Iterator<OpenAPIExtension> chain = OpenAPIExtensions.chain();
        return chain.hasNext() ? chain.next().extractParameters(annotations, type, new HashSet<>(), components, classConsumes, null, false, jsonViewAnnotation, chain).parameters :
                Collections.emptyList();
    }

    public static String getPath(At classLevelPath, ApiVersion classLevelApiVersion, At methodLevelPath, ApiVersion methodLevelApiVersion, String parentPath, boolean isSubresource, String methodName) {
        if (classLevelPath == null && methodLevelPath == null && StringUtils.isEmpty(parentPath)) {
            return null;
        }
        StringBuilder b = new StringBuilder();
        appendPathComponent(parentPath, b);
        if (classLevelPath != null && !isSubresource) {
            for (String path : classLevelPath.value()) {
                // 替换路径中的API版本号
                if (methodLevelApiVersion != null) {
                    path = path.replace("{version}", methodLevelApiVersion.value());
                } else if (classLevelApiVersion != null) {
                    path = path.replace("{version}", classLevelApiVersion.value());
                }
                appendPathComponent(path, b);
            }
        }
        if (methodLevelPath != null) {
            if (methodLevelPath.value().length > 0) {
                for (String path : methodLevelPath.value()) {
                    appendPathComponent(path, b);
                }
            } else {
                appendPathComponent(methodName, b);
            }
        }
        return b.length() == 0 ? "/" : b.toString();
    }

    /**
     * appends a path component string to a StringBuilder
     * guarantees:
     * <ul>
     *     <li>nulls, empty strings and "/" are nops</li>
     *     <li>output will always start with "/" and never end with "/"</li>
     * </ul>
     *
     * @param component component to be added
     * @param to        output
     */
    private static void appendPathComponent(String component, StringBuilder to) {
        if (component == null || component.isEmpty() || "/".equals(component)) {
            return;
        }
        if (!component.startsWith("/") && (to.length() == 0 || '/' != to.charAt(to.length() - 1))) {
            to.append("/");
        }
        if (component.endsWith("/")) {
            to.append(component, 0, component.length() - 1);
        } else {
            to.append(component);
        }
    }

    public static String extractOperationMethod(Method method, Iterator<OpenAPIExtension> chain) {
        if (method.getAnnotation(GET.class) != null) {
            return GET_METHOD;
        } else if (method.getAnnotation(PUT.class) != null) {
            return PUT_METHOD;
        } else if (method.getAnnotation(POST.class) != null) {
            return POST_METHOD;
        } else if (method.getAnnotation(DELETE.class) != null) {
            return DELETE_METHOD;
        } else if ((ReflectionUtils.getOverriddenMethod(method)) != null) {
            return extractOperationMethod(ReflectionUtils.getOverriddenMethod(method), chain);
        } else if (chain != null && chain.hasNext()) {
            return chain.next().extractOperationMethod(method, chain);
        } else {
            return null;
        }
    }

    public static String getFormatByType(String type) {

        if (type.toLowerCase().contains("integer")) {
            return "int32";
        } else if (type.toLowerCase().contains("float")) {
            return "float";
        } else if (type.toLowerCase().contains("long")) {
            return "int64";
        } else if (type.toLowerCase().contains("double")) {
            return "double";
        } else if (type.toLowerCase().contains("date")) {
            return "date";
        }

        return "";
    }

    public static String getParamType(String typeName) {
        if (typeName.toLowerCase().contains("string")) {
            return "string";
        } else if (typeName.toLowerCase().contains("integer")) {
            return "integer";
        } else if (typeName.toLowerCase().contains("float")) {
            return "number";
        } else if (typeName.toLowerCase().contains("long")) {
            return "integer";
        } else if (typeName.toLowerCase().contains("double")) {
            return "number";
        } else if (typeName.toLowerCase().contains("boolean")) {
            return "boolean";
        } else if (typeName.toLowerCase().contains("date")) {
            return "string";
        }
        return "object";
    }
}
