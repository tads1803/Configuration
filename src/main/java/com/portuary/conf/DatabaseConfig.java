/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.portuary.conf;

/**
 * Classe de parâmetros do Banco de Dados
 * @author Portuary
 */
public class DatabaseConfig extends ConfigBase{
    
    public String userDatabase = "portuary";
    public String nameDatabase = "portuary";
    public String passDatabase = "1q2w3e!Q@W#E";
    public String databaseUrl = "localhost";
    public String databasePort = "5431";
    public String databaseDriver = "org.postgresql.Driver";
    
    public Integer maximoPoolSize = 10;
    
    public Boolean cachePrepStmts = true;
    public Integer prepStmtCacheSize = 250;
    public Integer prepStmtCacheSqlLimit = 2048;
    
    public Integer maxLifeTime = 1800000;
    public Integer idleTimeout = 600000;
    public Integer connectionTimeout = 30000;
    public Integer validationTimeout = 5000;
    
}
