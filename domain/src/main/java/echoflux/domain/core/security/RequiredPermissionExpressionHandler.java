package echoflux.domain.core.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.expression.EvaluationContext;
    import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.function.Supplier;

public class RequiredPermissionExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
        var ctx = super.createEvaluationContext(authentication, mi);

        findRequiredPermissions(mi).ifPresent(ann ->
                ctx.setVariable("requiredPermissions", ann.value())
        );

        return ctx;
    }

    private static Optional<RequiredPermissions> findRequiredPermissions(MethodInvocation mi) {
        var targetClass = mi.getThis() != null
                ? AopUtils.getTargetClass(mi.getThis())
                : mi.getMethod().getDeclaringClass();

        var specificMethod = AopUtils.getMostSpecificMethod(mi.getMethod(), targetClass);

        var ann = AnnotatedElementUtils.findMergedAnnotation(specificMethod, RequiredPermissions.class);
        if (ann != null) {
            return Optional.of(ann);
        }

        if (!specificMethod.equals(mi.getMethod())) {
            ann = AnnotatedElementUtils.findMergedAnnotation(mi.getMethod(), RequiredPermissions.class);
            if (ann != null) {
                return Optional.of(ann);
            }
        }

        return Optional.ofNullable(
                AnnotatedElementUtils.findMergedAnnotation(targetClass, RequiredPermissions.class)
        );
    }

}
