package com.ppdai.codegen.demo.wire.demo.swagger;

/**
 * @author zhangchengxi
 * Date 2018/4/24
 */

import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;


public abstract class AbstractGenerator {
//    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenerator.class);

    @SuppressWarnings("static-method")
    public File writeToFile(String filename, String contents) throws IOException {
//        LOGGER.info("writing file " + filename);
        File output = new File(filename);

        if (output.getParent() != null && !new File(output.getParent()).exists()) {
            File parent = new File(output.getParent());
            parent.mkdirs();
        }
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(output), "UTF-8"));

        out.write(contents);
        out.close();
        return output;
    }

    public String readTemplate(String name) {
        try {
            Reader reader = getTemplateReader(name);
            if (reader == null) {
                throw new RuntimeException("no file found");
            }
            Scanner s = new Scanner(reader).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
        }
        throw new RuntimeException("can't load template " + name);
    }

    public Reader getTemplateReader(String name) {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(getCPResourcePath(name));
            if (is == null) {
                is = new FileInputStream(new File(name)); // May throw but never return a null value
            }
            return new InputStreamReader(is, "UTF-8");
        } catch (Exception e) {
//            LOGGER.error(e.getMessage());
        }
        throw new RuntimeException("can't load template " + name);
    }

    private String buildLibraryFilePath(String dir, String library, String file) {
        return dir + File.separator + "libraries" + File.separator + library + File.separator + file;
    }

    /**
     * Get the template file path with template dir prepended, and use the
     * library template if exists.
     *
     * @param codegen       Codegen config
     * @param templateFile Template file
     * @return String Full template file path
     */
    public String getFullTemplateFile(Codegen codegen, String templateFile) {
        //1st the code will check if there's a <template folder>/libraries/<library> folder containing the file
        //2nd it will check for the file in the specified <template folder> folder
        //3rd it will check if there's an <embedded template>/libraries/<library> folder containing the file
        //4th and last it will assume the file is in <embedded template> folder.

        //check the supplied template library folder for the file
        final String library = codegen.getLibrary();
        if (StringUtils.isNotEmpty(library)) {
            //look for the file in the library subfolder of the supplied template
            final String libTemplateFile = buildLibraryFilePath(codegen.templateDir(), library, templateFile);
            if (new File(libTemplateFile).exists()) {
                return libTemplateFile;
            }
        }

        //check the supplied template main folder for the file
        final String template = codegen.templateDir() + File.separator + templateFile;
        if (new File(template).exists()) {
            return template;
        }

        //try the embedded template library folder next
        if (StringUtils.isNotEmpty(library)) {
            final String embeddedLibTemplateFile = buildLibraryFilePath(codegen.embeddedTemplateDir(), library, templateFile);
            if (embeddedTemplateExists(embeddedLibTemplateFile)) {
                // Fall back to the template file embedded/packaged in the JAR file library folder...
                return embeddedLibTemplateFile;
            }
        }

        // Fall back to the template file embedded/packaged in the JAR file...
        return codegen.embeddedTemplateDir() + File.separator + templateFile;
    }

    public String readResourceContents(String resourceFilePath) {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(this.getClass().getResourceAsStream(getCPResourcePath(resourceFilePath)), "UTF-8");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    public boolean embeddedTemplateExists(String name) {
        return this.getClass().getClassLoader().getResource(getCPResourcePath(name)) != null;
    }

    @SuppressWarnings("static-method")
    public String getCPResourcePath(String name) {
        if (!"/".equals(File.separator)) {
            return name.replaceAll(Pattern.quote(File.separator), "/");
        }
        return name;
    }
}
