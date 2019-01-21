/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fr.sciforma;

import com.fr.sciforma.beans.Connector;
import com.fr.sciforma.exeception.TechnicalException;
import com.fr.sciforma.manager.ProjectManager;
import com.sciforma.psnext.api.Global;
import com.sciforma.psnext.api.PSException;
import com.sciforma.psnext.api.Project;
import com.sciforma.psnext.api.Session;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.pmw.tinylog.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 *
 * @author lahou
 */
public class Run {


    public static ApplicationContext ctx;

    private static String IP;
    private static String PORT;
    private static String CONTEXTE;
    private static String USER;
    private static String PWD;

    public static Session mSession;
    private static ProjectManager projectManager;

    private static Date start;
    private static Date finish;
    private static SimpleDateFormat sdf;

    private static final String PROGRAM = "ApplyTime";
    private static final String NUMBER = "1.1";

    public static void main(String[] args) {
        Logger.info("[main][" + PROGRAM + "][V" + NUMBER + "] Demarrage de l'API: " + new Date());
        if(args.length == 1)
        {
            try {
                initialisation();
                connexion(args[0]);
                chargementConfiguration();
                process();
                mSession.logout();
                Logger.info("[main][" + PROGRAM + "][V" + NUMBER + "] Fin de l'API: " + new Date());
            } catch (PSException ex) {
                Logger.error(ex);
            }
        }else{
            Logger.error("Erreur dans les arguments de jar");
        }
        System.exit(0);
    }

    private static void initialisation() {
        
        //ctx = new FileSystemXmlApplicationContext(System.getProperty("user.dir") + System.getProperty("file.separator") + "config" + System.getProperty("file.separator") + "applicationContext.xml");
        //Logger.info("[initialisation][" + path + "] " + new Date());
        //ctx = new FileSystemXmlApplicationContext(path);
        sdf = new SimpleDateFormat("dd/MM/yyyy");
    }

    private static void connexion(String path) {
        Properties properties = new Properties();
        FileInputStream in;
//        
//        Connector c = (Connector) ctx.getBean("sciforma");
//
//        USER = c.getUSER();
//        PWD = c.getPWD();
//        IP = c.getIP();
//        PORT = c.getPORT();
//        CONTEXTE = c.getCONTEXTE();

        try {
            in = new FileInputStream(path);
            properties.load(in);
            in.close();
            
            USER = properties.getProperty("sciforma.user");
            PWD = properties.getProperty("sciforma.pwd");
            IP = properties.getProperty("sciforma.ip");
            CONTEXTE = properties.getProperty("sciforma.ctx");
            
            Logger.info("Initialisation de la Session:" + new Date());
            String url = IP + "/" + CONTEXTE;
            mSession = new Session(url);
            mSession.login(USER, PWD.toCharArray());
            Logger.info("Connecté: " + new Date() + " à l'instance " + CONTEXTE);
        } catch (PSException ex) {
            Logger.error("Erreur dans la connection de ... " + CONTEXTE, ex);
            System.exit(-1);
        } catch(FileNotFoundException ex){
            Logger.error("Erreur dans la connection de ... " + CONTEXTE, ex);
            System.exit(-1);
        }catch(IOException ex){
            Logger.error("Erreur dans la connection de ... " + CONTEXTE, ex);
            System.exit(-1);
        }catch(NullPointerException ex){
            Logger.error("Erreur dans la connection de ... " + CONTEXTE, ex);
            System.exit(-1);
        }
        
    }

    private static void chargementConfiguration() {
        Logger.info("Demarrage du chargement des parametres de l'application:" + new Date());
        try {
            start = new Global().getDateField("AUT_application.debut");
            Logger.info("AUT_application.debut:" + start);
        } catch (PSException e) {
            String message = "ER.03 - Erreur lors de la récupération de la donnée : Impossible de récupérer AUT_application.debut";
            Logger.error(message, e);
            throw new TechnicalException(message);
        }
        try {
            finish = new Global().getDateField("AUT_application.fin");
            Logger.info("AUT_application.fin:" + finish);
        } catch (PSException e) {
            String message = "ER.03 - Erreur lors de la récupération de la donnée : Impossible de récupérer AUT_application.fin";
            Logger.error(message, e);
            throw new TechnicalException(message);
        }

    }

    private static void process() throws PSException {
        Project p = null;
        try {
            List<Project> lp = mSession.getProjectList(Project.VERSION_WORKING, Project.READWRITE_ACCESS);
            int nbProjet = lp.size();
            Iterator lpit = lp.iterator();
            while (lpit.hasNext()) {
                p = (Project) lpit.next();
               
                Logger.info("=======================================================================================");
                Logger.info("Traitement du projet [" + (lp.indexOf(p) + 1) + "/" + nbProjet + "] " + p.getStringField("Name"));
                Logger.info("=======================================================================================");
                try {
                    p.open(false);
                    if (p.getBooleanField("AUT_application")) {
                        if (start.compareTo(p.getDateField("Start")) > 0) {
                            p.applyTimesheets(start, finish, true, true, false);
                            Logger.info("Application des temps sur le projet " + p.getStringField("Name"));
                        } else {
                            Logger.warn("INVALID ! " + p.getStringField("Name") + " <" + sdf.format(p.getDateField("Start")) + "> et <" + sdf.format(start) + ">");
                        }
                    }
                    p.save();
                    try {
                        p.publish();
                        Logger.info(p.getStringField("Name") + " a été publié");
                    } catch (PSException e) {
                        Logger.error("ER.04 Le projet <" + p.getStringField("ID") + "> - <" + p.getStringField("Name") + " ne peut pas être publié car il ne respecte pas les critères du processus <" + p.getStringField("Workflow") + ">", e);
                    }
                    p.close();
                } catch (PSException ex) {
                    String message = "ER.02 Le projet est verrouillé";
                    Logger.error(message, ex);
                }
            }
        } catch (PSException ex) {
            String message = "ER.01 Problème survenu lors de la récupération de la liste des projets.";
            Logger.error(message, ex);
        } catch (Exception ex) {
            Logger.error(ex);
        } finally {
            p.close();
        }
    }

}
