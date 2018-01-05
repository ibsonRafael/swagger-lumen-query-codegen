package br.com.allwissend.codegen;

import io.swagger.codegen.languages.*;

import io.swagger.codegen.*;
import io.swagger.models.properties.*;

import java.util.*;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwLumenCodeGenerator extends AbstractPhpCodegen
{
    static Logger LOGGER = LoggerFactory.getLogger(AbstractPhpCodegen.class);

     @SuppressWarnings("hiding")
    protected String apiVersion = "1.0.0";

    /**
     * Configures the type of generator.
     *
     * @return  the CodegenType for this generator
     * @see     io.swagger.codegen.CodegenType
     */
    public CodegenType getTag() {
        return CodegenType.SERVER;
    }

    /**
     * Configures a friendly name for the generator.  This will be used by the generator
     * to select the library with the -l flag.
     *
     * @return the friendly name for the generator
     */
    public String getName() {
        return "q-lumen";
    }

    /**
     * Returns human-friendly help for the generator.  Provide the consumer with help
     * tips, parameters here
     *
     * @return A string value for the help message
     */
    public String getHelp() {
        return "Generates a LumenServerCodegen server library.";
    }

    public AwLumenCodeGenerator() {
        super();

        embeddedTemplateDir = templateDir = "AwLumenCodeGenerator";

        srcBasePath = "backend";
        String standIs = "query";

        /*
         * packPath
         */
        invokerPackage = "lumen";
        packagePath = "";

        /*
         * Api Package.  Optional, if needed, this can be used in templates
         */
        apiPackage = standIs + "." + "app.Http.Controllers";

        /*
         * Model Package.  Optional, if needed, this can be used in templates
         */
        modelPackage = standIs + "." + "app.Models";

        // template files want to be ignored
        modelTemplateFiles.clear();
        apiTestTemplateFiles.clear();
        apiDocTemplateFiles.clear();
        modelDocTemplateFiles.clear();

        modelTemplateFiles.put("model.mustache", ".php");
        modelDocTemplateFiles.put("model_doc.mustache", ".md");

        apiTemplateFiles.put("apiQuery.mustache", ".php");
        apiTestTemplateFiles.put("api_test.mustache", ".php");
        apiDocTemplateFiles.put("api_doc.mustache", ".md");

        /*
         * Additional Properties.  These values can be passed to the templates and
         * are available in models, apis, and supporting files
         */
        additionalProperties.put("apiVersion", apiVersion);

        /*
         * Supporting Files.  You can write single files for the generator with the
         * entire object tree available.  If the input file has a suffix of `.mustache
         * it will be processed by the template engine.  Otherwise, it will be copied
         */
        supportingFiles.add(new SupportingFile("readme.md", packagePath + File.separator + srcBasePath + File.separator + standIs , "readme.md"));
        supportingFiles.add(new SupportingFile("artisan", packagePath + File.separator + srcBasePath + File.separator + standIs , "artisan"));
        supportingFiles.add(new SupportingFile(".gitignore", packagePath + File.separator + srcBasePath + File.separator + standIs , ".gitignore"));
        supportingFiles.add(new SupportingFile("Jenkinsfile.mustache", packagePath + File.separator + srcBasePath + File.separator + standIs, "Jenkinsfile"));

        supportingFiles.add(new SupportingFile("composer.mustache", packagePath + File.separator + srcBasePath + File.separator + standIs, "composer.json"));
        supportingFiles.add(new SupportingFile(".htaccess", packagePath + File.separator + srcBasePath + File.separator + standIs, ".htaccess"));

        // Config files:
        String lumenConfigPath = packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "config";
        supportingFiles.add(new SupportingFile("config.broadcasting.php", lumenConfigPath, "broadcasting.php"));
        supportingFiles.add(new SupportingFile("config.database.php", lumenConfigPath, "database.php"));
        supportingFiles.add(new SupportingFile("config.queue.php", lumenConfigPath, "queue.php"));

        supportingFiles.add(new SupportingFile("app.php", packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "bootstrap", "app.php"));
        supportingFiles.add(new SupportingFile("index.php", packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "public", "index.php"));
        supportingFiles.add(new SupportingFile("User.php", packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "app", "User.php"));
        supportingFiles.add(new SupportingFile("Kernel.php", packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "app"  + File.separator + "Console", "Kernel.php"));
        supportingFiles.add(new SupportingFile("Handler.php", packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "app"  + File.separator + "Exceptions", "Handler.php"));
        supportingFiles.add(new SupportingFile("routes.mustache", packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "app"  + File.separator + "Http", "routes.php"));

        supportingFiles.add(new SupportingFile("Controller.php", packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "app"  + File.separator + "Http" + File.separator + "Controllers" + File.separator, "Controller.php"));
        supportingFiles.add(new SupportingFile("Authenticate.php", packagePath + File.separator + srcBasePath + File.separator + standIs + File.separator + "app"  + File.separator + "Http" + File.separator + "Middleware" + File.separator, "Authenticate.php"));

        // Docker
        String containerPath = packagePath + File.separator + "containers";
        String dockerBuildPath = containerPath + File.separator + "build";

        supportingFiles.add(new SupportingFile("docker-compose.mustache", packagePath, "docker-compose.yml"));
        supportingFiles.add(new SupportingFile("Dockerfile.php7",   dockerBuildPath + File.separator + "php7", "Dockerfile"));

    }

    // override with any special post-processing
    @Override
    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
        @SuppressWarnings("unchecked")
        Map<String, Object> objectMap = (Map<String, Object>) objs.get("operations");
        @SuppressWarnings("unchecked")
        List<CodegenOperation> operations = (List<CodegenOperation>) objectMap.get("operation");

        for (Iterator<CodegenOperation> iter = operations.listIterator(); iter.hasNext(); ) {
            CodegenOperation op = iter.next();
            op.httpMethod = op.httpMethod.toLowerCase();

            if (op.httpMethod != null && Objects.equals("post",op.httpMethod)) {
              iter.remove();
              LOGGER.warn("Removing " + op.path + ":" + op.operationId + ":" + op.httpMethod + " belongs to query.");
            } else if (op.httpMethod != null && (Objects.equals("put", op.httpMethod) || Objects.equals("patch", op.httpMethod))) {
              iter.remove();
              LOGGER.warn("Removing " + op.path + ":" + op.operationId + ":" + op.httpMethod + " belongs to query.");
            } else if (op.httpMethod != null && Objects.equals("delete",op.httpMethod)) {
              iter.remove();
              LOGGER.warn("Removing " + op.path + ":" + op.operationId + ":" + op.httpMethod + " belongs to query.");
            }
        }

        for (CodegenOperation op : operations) {
            op.httpMethod = op.httpMethod.toLowerCase();
            // check to see if the path contains ".", which is not supported by Lumen
            // ref: https://github.com/swagger-api/swagger-codegen/issues/6897
            if (op.path != null && op.path.contains(".")) {
                throw new IllegalArgumentException("'.' (dot) is not supported by PHP Lumen. Please refer to https://github.com/swagger-api/swagger-codegen/issues/6897 for more info.");
            }
        }

        // sort the endpoints in ascending to avoid the route priority issure.
        // https://github.com/swagger-api/swagger-codegen/issues/2643
        Collections.sort(operations, new Comparator<CodegenOperation>() {
            @Override
            public int compare(CodegenOperation lhs, CodegenOperation rhs) {
                return lhs.path.compareTo(rhs.path);
            }
        });

        return objs;
    }
}
