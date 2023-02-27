/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.portuary.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Objects;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 *
 * @author Portuary
 */
public class ConfigManager {

    private static final ConfigManager fileManager = new ConfigManager();
    
    protected final Logger logger = LogManager.getLogger(getClass());
        
    // Versão
    private final Double NEW_VERSION = 1.11;
    
    // Path 
    private final String LOCAL_PATH = "usr/local/";
    private final String COMPANY_PATH = "portuary/";
    
    // Armazena o nome da solução em questão
    private final String solutionName = "webapp/";
    
    // Armazena o nome do projeto em questão
    private final String projectName = "cloud/";
    
    // Armazena as configurações do banco de dados
    public DatabaseConfig dataBaseConfig = new DatabaseConfig();
    
    // Armazena as configurações da aplicação
    public AppWebConfig appWebConfig = new AppWebConfig();
    
    // Enum com os path padrão do sistema
    private enum ListPaths {
        LOG_PATH("logs/"),
        CONF_PATH("conf/"),
        SQL_SCRIPTS_PATH("sitio_sql_scripts/"),
        MEDIA_PATH("media/"),
        IMAGEM_PESSOA_PATH(MEDIA_PATH + "pessoas/");

        private final String path;

        ListPaths(String path) {
            this.path = path;
        }

        @Override
        public String toString() {
            return path;
        }        
    }

    /**
     * Construtor
     */
    private ConfigManager(){
        try{ openConfDataBase(); }catch(Exception ex){ logger.error("Erro ao abrir as configurações de banco de dados!",ex); }
        try{ openConfAppWeb(); }catch(Exception ex){ logger.error("Erro ao abrir as configurações de SMTP!",ex); }
    }
    
    /**
     * Retorna a instância do file manager
     * @return ConfigManager
     */
    public static ConfigManager getInstance(){
        return fileManager;
    }
    
    /**
     * Retorna a unidade do driver em que se encontra o arquivo Ex: windows("C:/") linux("/")
     * @return String contendo a letra/referência da unidade
     */
    public String getOSDriverUnit() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().startsWith("windows")) {
            return "C:/";
        } else {
            return "/";
        }
    }

    /**
     * Verifica se o caminho do arquivo/diretório existe
     * @param fileName Caminho do diretório ou arquivo.
     * @return <code>true</code> se o caminho ou arquivo existe ou
     * <code>false</code> caso não exista.
     */
    public boolean fileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
    
    /**
     * Escreve em disco o arquivo com as configurações Ex.: "fileName.conf"
     * @param fileName String contendo o nome do arquivo Ex.: "database" "server"
     * @param obj ConfigBase com os dados a serem gravados em disco
     * @throws java.lang.Exception
     */
    public void writeConfigFile(String fileName, ConfigBase obj) throws Exception{        
        // Verifica se foi informado um nome para o arquivo, caso contrário retorna uma Exception
        if (fileName.isEmpty()) throw new Exception("O nome do arquivo não pode ser null!");
        
        String fullDirectory = getOSDriverUnit() + LOCAL_PATH + COMPANY_PATH + solutionName + projectName + ListPaths.CONF_PATH;
        // Obtém o caminho completo do arquivo
        String fullFileName = fullDirectory + fileName + ".conf";
        // Instacia responsável pelo gerenciamento do arquivo
        File file = new File(fullFileName);
        // Tenta criar os diretórios necessários
        (new File(fullDirectory)).mkdirs();
        // Verifica se o arquivo existe, caso contrário, ele cria um novo
        if (!file.exists()) file.createNewFile();
        
        try(OutputStream outputStream = new FileOutputStream(file, false)){
            
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson g = gsonBuilder.setPrettyPrinting().create();
            
            outputStream.write(g.toJson(obj).getBytes());
            outputStream.flush();
        }catch (Exception ex){
            System.out.println(ex);
            throw ex;
        }
    }
    
    /**
     * Obtém do disco o arquivo com as configurações Ex.: "fileName.conf"
     * @param fileName String com o nome do arquivo Ex.: "database" "server"
     * @param obj ConfigBase para preencher com o conteúdo do arquivo
     * @return ConfigBase
     * @throws Exception
     */
    public ConfigBase readConfigFile(String fileName, ConfigBase obj) throws Exception {
        // Verifica se foi informado um nome para o arquivo, caso contrário retorna uma Exception
        if (fileName.isEmpty()) throw new Exception("O nome do arquivo não pode ser null!");
        // Obtém o caminho completo do arquivo
        String fullFileName = getOSDriverUnit() + LOCAL_PATH + COMPANY_PATH + solutionName + projectName + ListPaths.CONF_PATH + fileName + ".conf";        
        // Instacia responsável pelo gerenciamento do arquivo
        File file = new File(fullFileName);
        // Verifica se o arquivo existe, caso contrário, ele cria um novo
        if (!file.exists()) writeConfigFile(fileName, obj);
        
        try (Reader reader = new FileReader(fullFileName)){
            
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
            Gson g = gsonBuilder.setPrettyPrinting().create();
            
            // Converte os dados do arquivo em objeto
            ConfigBase conf = g.fromJson(reader, obj.getClass());
            // Verifica se a versão é a mesma
            if (!Objects.isNull(conf) && !conf.version.equals(NEW_VERSION)){
                conf.version = NEW_VERSION;
                writeConfigFile(fileName, conf);
            }
            
            return conf;
        } catch (IOException ex) {
            throw ex;
        }
    }
        
    /**
     * Salva o arquivo de configuração especificado, caso não exista, cria um novo com as configurações pré-definidas.
     * @throws Exception
     */
    public void saveConfDataBase() throws Exception {
        writeConfigFile("database",dataBaseConfig);
    }
    
    /**
     * Carrega o arquivo de configuração especificado, caso não exista, cria um novo com as configurações pré-definidas.
     * @throws Exception
     */
    public void openConfDataBase() throws Exception {
        dataBaseConfig = (DatabaseConfig)readConfigFile("database", dataBaseConfig);
    }
    
    /**
     * Salva o arquivo de configuração especificado, caso não exista, cria um novo com as configurações pré-definidas.
     * @throws Exception
     */
    public void saveConfAppWeb() throws Exception {
        writeConfigFile("AppWeb",appWebConfig);
    }
    
    /**
     * Carrega o arquivo de configuração especificado, caso não exista, cria um novo com as configurações pré-definidas.
     * @throws Exception
     */
    public void openConfAppWeb() throws Exception {
        appWebConfig = (AppWebConfig)readConfigFile("AppWeb",appWebConfig);
    }
    
    /**
     * Procedimento utilizado para deletar o arquivo de configuração
     * @param filePath String contendo a url do arquivo a ser deletado
     * @exception Exception
     */
    public void deleteConfigFile(String filePath) throws Exception{
        File file = new File(filePath);
        try { file.delete(); } 
        catch (Exception ex) { throw ex; }
    }
}
