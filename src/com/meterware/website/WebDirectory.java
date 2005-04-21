package com.meterware.website;
/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2005, Russell Gold
 *
 *******************************************************************************************************************/
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author <a href="mailto:russgold@meterware.com">Russell Gold</a>
 */
public class WebDirectory extends MenuTarget {

    private String _dir;
    private ArrayList _elements = new ArrayList();
    private ArrayList _pages = new ArrayList();


    public void generate( PageGenerator generator, SiteTemplate template, Site site ) {
        for (Iterator iterator = getPages().iterator(); iterator.hasNext();) {
            WebPage page = (WebPage) iterator.next();
            page.generate( generator, getPageTemplate( template ), site );
        }
    }


    /**
     * Returns the web pages associated with this directory.
     */
    protected ArrayList getPages() {
        if (_dir == null) throw new IllegalStateException( "No source dir specified for web directory" );

        if (_pages == null || _pages.size() != _elements.size()) {
            _pages = new ArrayList();
            File dir = new File( PageFragment.getRoot(), _dir );

            for (Iterator iterator = _elements.iterator(); iterator.hasNext();) {
                Element element = (Element) iterator.next();
                String[] sources = dir.list( new FragmentFilter( element ) );
                if (sources.length == 0) throw new RuntimeException( "No source found for " + element.getLocation() );
                WebPage page = new WebPage();
                final PageFragment fragment = page.createFragment();
                fragment.setSource( _dir + '/' + sources[0] );
                page.setLocation( _dir + '/' + element.getLocation() + ".html" );
                _pages.add( page );
            }
        }
        return _pages;
    }


    static class FragmentFilter implements FilenameFilter {

        private String _prefix;


        public FragmentFilter( Element element ) {
            _prefix = element.getLocation() + '.';
        }


        public boolean accept( File directory, String fileName ) {
            return fileName.startsWith( _prefix );
        }
    }


    public void appendMenuItem( StringBuffer sb, SiteTemplate template, String currentLocation ) {
        Element element = getFirstElement();
        template.appendMenuItem( sb, currentLocation, element.getTitle(), _dir + '/' + element.getLocation() + ".html" );
    }


    private Element getFirstElement() {
        return (Element) _elements.iterator().next();
    }


    public void setDir( String dir ) {
        _dir = dir;
    }


    public Element createElement() {
        return addElement( new Element() );
    }


    protected Element addElement( Element element ) {
        _elements.add( element );
        return element;
    }


    protected SiteTemplate getPageTemplate( SiteTemplate defaultTemplate ) {
        return defaultTemplate;
    }
    

    public class Element {

        private String _location;
        private String _title;


        public void setLocation( String location ) {
            _location = location;
        }


        public void setTitle( String title ) {
            _title = title;
        }


        String getLocation() {
            return _location;
        }


        String getTitle() {
            return _title;
        }
    }
}
