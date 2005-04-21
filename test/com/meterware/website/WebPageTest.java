package com.meterware.website;
/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2005, Russell Gold
 *
 *******************************************************************************************************************/
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.jmock.Mock;

import java.io.File;
import java.io.IOException;



/**
 * @author <a href="mailto:russgold@meterware.com">Russell Gold</a>
 */
public class WebPageTest extends QuickSiteTestCase {

    public static void main( String[] args ) {
        TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        return new TestSuite( WebPageTest.class );
    }


    /**
     * Verify that a web page generates a single file, given one fragment.
     */
    public void testSingleFragmentToOnePage() throws Exception {
        final String CONTENTS = "A simple string";
        final String location = "single.html";

        File textFile = createTextFile( CONTENTS );
        WebPage webPage = new WebPage();
        webPage.createFragment().setSource( textFile.getAbsolutePath() );
        webPage.setLocation( location );
        webPage.setItem( "an item" );

        Mock mockPageGenerator = new Mock( PageGenerator.class );
        Mock mockSiteTemplate = new Mock( SiteTemplate.class );

        Site site = new Site();
        mockPageGenerator.expects( once() ).method( "definePageAt" ).with( eq( location ), eq( CONTENTS + FragmentTemplate.LINE_BREAK ) );
        mockSiteTemplate.expects( once() ).method( "appendPageHeader" ).with( super.isA( StringBuffer.class ), same( site ), same( webPage ) );
        mockSiteTemplate.expects( once() ).method( "appendPageFooter" ).with( super.isA( StringBuffer.class ), same( site ), same( webPage ) );

        webPage.generate( (PageGenerator) mockPageGenerator.proxy(), (SiteTemplate) mockSiteTemplate.proxy(), site );

        StringBuffer buffer = new StringBuffer();
        mockSiteTemplate.expects( once() ).method( "appendMenuItem" ).with( same( buffer ), eq( "elsewhere" ), eq( "an item"), eq( location ) );

        webPage.appendMenuItem( buffer, (SiteTemplate) mockSiteTemplate.proxy(), "elsewhere" );

        mockPageGenerator.verify();
        mockSiteTemplate.verify();
    }


    /**
     * Verify that a web page generates a single file, given two fragments. The simulated configuration is:
     * <web-page location="single.html">
     *    <fragment src="fragment1.txt" />
     *    <fragment src="fragment2.txt" />
     * </web-page>
     */
    public void testTwoFragmentsToOnePage() throws Exception {
        final String content1 = "A simple string";
        final String content2 = "Something more";
        final String location = "single.html";

        WebPage webPage = new WebPage();
        webPage.createFragment().setSource( createTextFile( content1 ).getAbsolutePath() );
        webPage.createFragment().setSource( createTextFile( content2 ).getAbsolutePath() );
        webPage.setLocation( location );
        Site site = new Site();

        Mock mockPageGenerator = new Mock( PageGenerator.class );
        Mock mockSiteTemplate = new Mock( SiteTemplate.class );

        mockPageGenerator.expects( once() ).method( "definePageAt" ).with( eq( location ), eq( content1 + FragmentTemplate.LINE_BREAK + content2 + FragmentTemplate.LINE_BREAK ) );
        mockSiteTemplate.expects( once() ).method( "appendPageHeader" ).with( super.isA( StringBuffer.class ), same( site ), same( webPage ) );
        mockSiteTemplate.expects( once() ).method( "appendPageFooter" ).with( super.isA( StringBuffer.class ), same( site ), same( webPage ) );

        webPage.generate( (PageGenerator) mockPageGenerator.proxy(), (SiteTemplate) mockSiteTemplate.proxy(), site );

        mockPageGenerator.verify();
        mockSiteTemplate.verify();
    }


    /**
     * Verify that a web directory generates a file for each source file.
     * <directory item="Click Me" dir="group">
     *    <element location="fragment1" title="Alpha" />
     *    <element location="fragment2" title="Beta" />
     *    <element location="fragment3" title="Gamma" />
     * </directory>
     *
     * ?? maybe have index.txt in each directory with this info, plus <subdir> element to select more (later?)
     */
    public void testSimpleWebDirectory() throws Exception {
        final File sourceDir = new File( System.getProperty( "java.io.tmpdir" ) );
        File groupDir = new File( sourceDir, "group" );
        if (!groupDir.exists()) {
            groupDir.mkdir();
        } else {
            clearDir( groupDir );
        }

        GroupEntry[] entries = new GroupEntry[] { new GroupEntry( groupDir, "Alpha", "something" ),
                                                  new GroupEntry( groupDir, "Beta", "other thing" ),
                                                  new GroupEntry( groupDir, "Gamma", "yet another" ) };

        WebDirectory directory = new WebDirectory();
        directory.setItem( "Click Me" );
        directory.setDir( "group" );
        for (int i = 0; i < entries.length; i++) {
            GroupEntry entry = entries[i];
            entry.addEntry( directory );
        }
        PageFragment.setRoot( sourceDir );
        Site site = new Site();

        assertEquals( "Num pages defined", 3, directory.getPages().size() );
        Mock mockPageGenerator = new Mock( PageGenerator.class );
        Mock mockSiteTemplate = new Mock( SiteTemplate.class );

        mockPageGenerator.expects( once() ).method( "definePageAt" ).with( eq( entries[0].getExpectedLocation() ), eq( "something" + FragmentTemplate.LINE_BREAK ) );
        mockPageGenerator.expects( once() ).method( "definePageAt" ).with( eq( entries[1].getExpectedLocation() ), eq( "other thing" + FragmentTemplate.LINE_BREAK ) );
        mockPageGenerator.expects( once() ).method( "definePageAt" ).with( eq( entries[2].getExpectedLocation() ), eq( "yet another" + FragmentTemplate.LINE_BREAK ) );
        mockSiteTemplate.expects( atLeastOnce() ).method( "appendPageHeader" ).with( isA( StringBuffer.class ), same( site ), isA( WebPage.class ) );
        mockSiteTemplate.expects( atLeastOnce() ).method( "appendPageFooter" ).with( isA( StringBuffer.class ), same( site ), isA( WebPage.class ) );

        directory.generate( (PageGenerator) mockPageGenerator.proxy(), (SiteTemplate) mockSiteTemplate.proxy(), site );

        StringBuffer buffer = new StringBuffer();
        mockSiteTemplate.expects( once() ).method( "appendMenuItem" ).with( same( buffer ), eq( "elsewhere" ), eq( "Alpha" ), eq( entries[0].getExpectedLocation() ) );
        directory.appendMenuItem( buffer, (SiteTemplate) mockSiteTemplate.proxy(), "elsewhere" );

        mockPageGenerator.verify();
        mockSiteTemplate.verify();
    }


    private String withoutExtension( String name ) {
        if (name.indexOf( '.' ) < 0) return name;
        return name.substring( 0, name.indexOf( '.' ) );
    }


    class GroupEntry {
        private File _fragment;
        private String _location;
        private String _title;


        GroupEntry( File dir, String title, String contents ) throws IOException {
            _title = title;
            _fragment = createTextFile( dir, contents );
            _location = withoutExtension( _fragment.getName() );
        }


        void addEntry( WebDirectory directory ) {
            WebDirectory.Element element = directory.createElement();
            element.setLocation( _location );
            element.setTitle( _title );
        }


        String getExpectedLocation() {
            return "group/" + withoutExtension( _fragment.getName() ) + ".html";
        }
    }


}
