package ma.emsi.yassine.atiki;

import okhttp3.Interceptor;
import okhttp3.Response;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/** Capture les corps HTTP réels, sans enregistrer les en-têtes ni la clé Gemini. */
public class TraceJsonGemini implements Interceptor {
    private static final Logger LOG = LoggerFactory.getLogger(TraceJsonGemini.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        var request = chain.request();
        if (request.body() != null) {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            LOG.info("GEMINI REQUETE JSON BRUT : {}", buffer.readUtf8());
        }
        Response response = chain.proceed(request);
        // peekBody copie le corps sans le consommer : le SDK lit la réponse normalement.
        LOG.info("GEMINI REPONSE HTTP {} JSON BRUT : {}",
                response.code(), response.peekBody(Long.MAX_VALUE).string());
        return response;
    }
}
