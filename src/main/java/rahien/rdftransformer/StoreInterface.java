package rahien.rdftransformer;

import org.openrdf.model.Resource;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: karel
 * Date: 7/28/13
 * Time: 10:27 AM
 */
public class StoreInterface {
    private Repository repos;
    private RepositoryConnection connection;


    public StoreInterface(){
        try{
            repos=new SailRepository(new MemoryStore());
            repos.initialize();

            connection=repos.getConnection();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cleanup(){
        try{
            connection.close();
            repos.shutDown();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getLocalUriForFilename(String filename){
        return "file://"+filename;
    }

    public void loadFile(String filename){
        try {
            File file = new File(filename);
            Resource contextURI = connection.getValueFactory().createURI(getLocalUriForFilename(filename));
            Resource[] contexts = new Resource[]{contextURI};

            connection.clear(contexts);
            connection.add(file, "http://localhost/", RDFFormat.forFileName(filename,RDFFormat.RDFXML), contexts);
        }catch (RepositoryException e) {
            e.printStackTrace();
        } catch (RDFParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectQuery(String sparql, OutputStream stream){
        try{
            connection.prepareTupleQuery(QueryLanguage.SPARQL, sparql).evaluate(new SPARQLResultsCSVWriter(stream));
            stream.flush();
        } catch (TupleQueryResultHandlerException e) {
            e.printStackTrace();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void graphQuery(String sparql, OutputStream stream){
        try{
            connection.prepareGraphQuery(QueryLanguage.SPARQL, sparql).evaluate(new TurtleWriter(stream));
            stream.flush();
        } catch (QueryEvaluationException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RDFHandlerException e) {
            e.printStackTrace();
        }
    }
}
