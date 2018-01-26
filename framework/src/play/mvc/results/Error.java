package play.mvc.results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import play.exceptions.TemplateNotFoundException;
import play.exceptions.UnexpectedException;
import play.libs.MimeTypes;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Scope;
import play.templates.TemplateLoader;

import java.util.Map;

/**
 * 500 Error
 */
public class Error extends Result {
    private static final Logger logger = LoggerFactory.getLogger(Error.class);

    private final int status;

    public Error(String reason) {
        super(reason);
        this.status = Http.StatusCode.INTERNAL_ERROR;
    }

    public Error(int status, String reason) {
        super(reason);
        this.status = status;
    }

    @Override
    public void apply(Request request, Response response) {
        response.status = status;
        String format = request.format;
        if (request.isAjax() && "html".equals(format)) {
            format = "txt";
        }
        response.contentType = MimeTypes.getContentType("xx." + format);
        Map<String, Object> binding = Scope.RenderArgs.current().data;
        binding.put("exception", this);
        binding.put("result", this);
        binding.put("session", Scope.Session.current());
        binding.put("request", request);
        binding.put("response", response);
        binding.put("flash", Scope.Flash.current());
        binding.put("params", Scope.Params.current());
        binding.put("play", new Play());
        
        String templatePath = "errors/" + this.status + "." + (format == null ? "html" : format);
        String errorHtml;
        try {
            errorHtml = TemplateLoader.load(templatePath).render(binding);
        } catch (TemplateNotFoundException noTemplateInDesiredFormat) {
            errorHtml = getMessage();
        } catch (Exception e) {
            logger.error("Failed to render {}", templatePath, e);
            errorHtml = getMessage();
        }
        try {
            response.out.write(errorHtml.getBytes(response.encoding));
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    public int getStatus() {
        return status;
    }
}
