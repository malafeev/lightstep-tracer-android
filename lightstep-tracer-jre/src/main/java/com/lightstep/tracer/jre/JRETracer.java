package com.lightstep.tracer.jre;

import com.lightstep.tracer.shared.AbstractTracer;
import com.lightstep.tracer.shared.Options;
import com.lightstep.tracer.shared.SimpleFuture;

import static com.lightstep.tracer.shared.Version.LIGHTSTEP_TRACER_VERSION;

public class JRETracer extends AbstractTracer {

    private static final int DEFAULT_REPORTING_INTERVAL_MILLIS = 2500;

    private static class JavaTracerHolder {
        private static final JRETracer INSTANCE = new JRETracer(null);
    }

    /**
     * Returns the singleton Tracer instance that can be utilized to record logs and spans.
     *
     * @return tracer instance
     */
    @SuppressWarnings("unused")
    public static JRETracer getInstance() {
        return JavaTracerHolder.INSTANCE;
    }

    public JRETracer(Options options) {
        super(AbstractTracer.setDefaultReportingIntervalMillis(options, DEFAULT_REPORTING_INTERVAL_MILLIS));
        addStandardTracerTags();
    }

    // Flush any data stored in the log and span buffers
    protected SimpleFuture<Boolean> flushInternal(boolean explicitRequest) {
        return new SimpleFuture<>(sendReport(explicitRequest));
    }

    protected void printLogToConsole(InternalLogLevel level, String msg, Object payload) {
        String s;
        switch (level) {
            case DEBUG:
                s = "[Lightstep:DEBUG] ";
                break;
            case INFO:
                s = "[Lightstep:INFO] ";
                break;
            case WARN:
                s = "[Lightstep:WARN] ";
                break;
            case ERROR:
                s = "[Lightstep:ERROR] ";
                break;
            default:
                s = "[Lightstep:???] ";
                break;
        }
        s += msg;
        if (payload != null) {
            s += " " + payload.toString();
        }
        System.err.println(s);
    }

    /**
     * Adds standard tags set by all LightStep client libraries.
     */
    private void addStandardTracerTags() {
        // The platform is called "jre" rather than "Java" to clearly
        // differentiate this library from the Android version
        addTracerTag(LIGHTSTEP_TRACER_PLATFORM_KEY, "jre");
        addTracerTag(LIGHTSTEP_TRACER_PLATFORM_VERSION_KEY, System.getProperty("java.version"));
        addTracerTag(LIGHTSTEP_TRACER_VERSION_KEY, LIGHTSTEP_TRACER_VERSION);
    }
}
