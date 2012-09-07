package com.meterware.website;
/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2003,2005 Russell Gold
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 *******************************************************************************************************************/
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;



/**
 *
 * @author <a href="mailto:russgold@meteware.com">Russell Gold</a>
 **/
public class WebPage extends MenuTarget implements SiteLocation {

    private String _title;
    private boolean _mainPage;

    private ArrayList _fragments = new ArrayList();


    public String getTitle() {
        return _title;
    }


    public String getLocation() {
        return super.getLocation();    //To change body of overridden methods use File | Settings | File Templates.
    }


    public void setTitle( String title ) {
        _title = title;
    }


    public String getItem() {
        String item = super.getItem();
        return item != null ? item : _title;
    }


    public void setMainPage( boolean mainPage ) {
        _mainPage = mainPage;
    }


    public boolean isMainPage() {
        return _mainPage;
    }


    public PageFragment createFragment() {
        PageFragment fragment = new PageFragment();
        _fragments.add( fragment );
        return fragment;
    }


    public void generate( PageGenerator generator, SiteTemplate template, Site site ) {
        generate( generator, template, site, null );
    }


    public void generate( PageGenerator generator, SiteTemplate template, Site site, String relativeRoot ) {
        try {
            StringBuffer sb = new StringBuffer();
            template.appendPageHeader( sb, site, this );
            for (int i = 0; i < _fragments.size(); i++) {
                PageFragment pageFragment = (PageFragment) _fragments.get( i );
                sb.append( pageFragment.asText( relativeRoot ) ).append( FragmentTemplate.LINE_BREAK );
            }
            template.appendPageFooter( sb, site, this );

            String location = getLocation();
            if (relativeRoot != null) location = relativeRoot + '/' + location;
            generator.definePageAt( location, sb.toString() );
        } catch (IOException e) {
            throw new RuntimeException( "Error writing page '" + e );
        }
    }


    public void appendMenuItem( StringBuffer sb, SiteTemplate template, String currentLocation ) {
        template.appendMenuItem( sb, currentLocation, getItem(), getLocation() );
    }


    public void setSource( String source ) {
        createFragment().setSource( source );
    }

}
