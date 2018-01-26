package play.mvc.results;

import com.thoughtworks.xstream.XStream;
import org.w3c.dom.Document;
import play.exceptions.UnexpectedException;
import play.libs.XML;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

/**
 * 200 OK with a text/xml
 */
public class RenderXml extends Result {

    private final String xml;

    public RenderXml(CharSequence xml) {
        this.xml = xml.toString();
    }

    public RenderXml(Document document) {
        this.xml = XML.serialize(document);
    }

    public RenderXml(Object o, XStream xstream) {
        this.xml = xstream.toXML(o);
    }

    public RenderXml(Object o) {
        this(o, new XStream());
    }

    @Override
    public void apply(Request request, Response response) {
        try {
            setContentTypeIfNotSet(response, "text/xml");
            response.out.write(xml.getBytes(response.encoding));
        } catch(Exception e) {
            throw new UnexpectedException(e);
        }
    }

    public String getXml() {
        return xml;
    }
}
