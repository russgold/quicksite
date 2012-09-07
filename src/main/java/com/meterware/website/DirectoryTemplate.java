package com.meterware.website;
/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2005, Russell Gold
 *
 *******************************************************************************************************************/
import java.util.*;
import java.io.IOException;

/**
 * @author <a href="mailto:russgold@meterware.com">Russell Gold</a>
 */
abstract public class DirectoryTemplate {

    private static ArrayList _templates = new ArrayList();


    public static void registerTemplate( DirectoryTemplate template ) {
        _templates.add( template );
    }


    static DirectoryTemplate getTemplateFor( String nodeName ) {
        for (int i = 0; i < _templates.size(); i++) {
            DirectoryTemplate template = (DirectoryTemplate) _templates.get( i );
            if (template.getRootNodeName().equals( nodeName )) return template.newFragment();
        }
        throw new RuntimeException( "No template defined for root node " + nodeName );
    }

    abstract public void generate( PageGenerator generator, SiteTemplate template, Site site ) throws IOException;


    abstract public DirectoryTemplate newFragment();


    abstract public String getRootNodeName();


    protected void definePage( PageGenerator generator, SiteTemplate siteTemplate, Site site, Location location, String pageContents ) throws IOException {
        StringBuffer sb = new StringBuffer();
        siteTemplate.appendPageHeader( sb, site, location );
        sb.append( pageContents );
        siteTemplate.appendPageFooter( sb, site, location );
        generator.definePageAt( location.getLocation(), sb.toString() );
    }


    private String getDirectoryPrefix( String dir ) {
        return dir.endsWith( "/" ) ? dir : dir + '/';
    }


    public abstract String getFirstLocation();


    public abstract void setBase( String dir, String name );


    protected class Location implements SiteLocation {
        private String _location;
        private String _title;


        public Location( String title, String dir, String relativeLocation ) {
            _title = title;
            _location = getDirectoryPrefix( dir ) + relativeLocation;
        }


        public String getLocation() {
            return _location;
        }


        public String getTitle() {
            return _title;
        }
    }
}
