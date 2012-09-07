package com.meterware.website;
/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2005, Russell Gold
 *
 *******************************************************************************************************************/
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.meterware.xml.DocumentSemantics;


/**
 * @author <a href="mailto:russgold@meterware.com">Russell Gold</a>
 */
public class WebCategory extends MenuTarget {

    private ArrayList _directories = new ArrayList();


    public Directory createDirectory() {
        Directory directory = new Directory();
        _directories.add( directory );
        return directory;
    }


    public void generate( PageGenerator generator, SiteTemplate template, Site site ) {
        for (Iterator iterator = _directories.iterator(); iterator.hasNext();) {
            Directory directory = (Directory) iterator.next();
            directory.getTemplate();
        }
        for (Iterator iterator = _directories.iterator(); iterator.hasNext();) {
            Directory directory = (Directory) iterator.next();
            directory.generate( generator, template, site );
        }
    }


    public void appendMenuItem( StringBuffer sb, SiteTemplate template, String currentLocation ) {
        Directory firstDirectory = (Directory) _directories.get(0);
        template.appendMenuItem( sb, currentLocation, getItem(), firstDirectory.getLocation() );
    }


    public class Directory {

        private String _dir;
        private String _name;
        private DirectoryTemplate _template;


        public void setDir( String dirPath ) {
            _dir = dirPath;
        }


        public void setName( String name ) {
            _name = name;
        }


        public void generate( PageGenerator generator, SiteTemplate siteTemplate, Site site ) {
            try {
                getTemplate().generate( generator, siteTemplate, site );
            } catch (IOException e) {
                throw new RuntimeException( "Error generating directory at " + getLocation() + ": " + e );
            }
        }


        public String getLocation() {
            return getTemplate().getFirstLocation();
        }


        DirectoryTemplate getTemplate() {
            if (_template == null) {
                File indexFile = null;
                try {
                    indexFile = new File( new File( PageFragment.getRoot(), _dir), "index.xml" );
                    if (!indexFile.exists()) throw new RuntimeException( indexFile.getAbsolutePath() + " does not exist" );

                    Document document = DocumentSemantics.parseDocument( indexFile );
                    _template = DirectoryTemplate.getTemplateFor( DocumentSemantics.getRootNode( document ).getNodeName() );
                    DocumentSemantics.build( document, _template, indexFile.getAbsolutePath() );
                    _template.setBase( _dir, _name );
                } catch (SAXException e) {
                    throw new RuntimeException( "Error parsing " + indexFile + ": " + e );
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException( "Error reading " + indexFile + ": " + e );
                }
            }
            return _template;
        }
    }


 }
