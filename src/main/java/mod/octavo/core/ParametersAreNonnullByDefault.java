package mod.octavo.core;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@NotNull
@Retention(value=RUNTIME)
public @interface ParametersAreNonnullByDefault{}