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
public class WebPageTest extends QuickSiteTestBase {

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
     * Verify that a web directory uses a separate index file to generate results.
     * <category item="teams">
     *    <directory name="crimson" dir="group/red"/>
     * </category>
     *
     * group/red/index.xml:
     * <?xml version='1.0'?>
     * <group-contents/>
     *
     */
    public void testSimpleWebCategory() throws Exception {
        final File sourceDir = new File( System.getProperty( "java.io.tmpdir" ) );
        File groupDir = new File( sourceDir, "group/red" );
        if (!groupDir.exists()) {
            groupDir.mkdirs();
        } else {
            clearDir( groupDir );
        }

        createTextFile( groupDir, "index.xml", "<?xml version='1.0'?><group-contents/>" );

        WebCategory category = new WebCategory();
        category.setItem( "teams" );
        WebCategory.Directory directory = category.createDirectory();
        directory.setDir( "group/red" );
        directory.setName( "crimson" );

        PageFragment.setRoot( sourceDir );
        Site site = new Site();

        Mock mockPageGenerator = new Mock( PageGenerator.class );
        Mock mockSiteTemplate = new Mock( SiteTemplate.class );

        mockPageGenerator.expects( once() ).method( "definePageAt" ).with( eq( "group/red/contents.html" ), eq( "something" ) );
        mockSiteTemplate.expects( once() ).method( "appendPageHeader" ).with( isA( StringBuffer.class ), same( site ), isA( SiteLocation.class ) );
        mockSiteTemplate.expects( once() ).method( "appendPageFooter" ).with( isA( StringBuffer.class ), same( site ), isA( SiteLocation.class ) );

        DirectoryTemplate.registerTemplate( new GroupContents() );
        category.generate( (PageGenerator) mockPageGenerator.proxy(), (SiteTemplate) mockSiteTemplate.proxy(), site );

        StringBuffer buffer = new StringBuffer();
        mockSiteTemplate.expects( once() ).method( "appendMenuItem" ).with( same( buffer ), eq( "elsewhere" ), eq( "teams" ), eq( "group/red/contents.html" ) );
        category.appendMenuItem( buffer, (SiteTemplate) mockSiteTemplate.proxy(), "elsewhere" );

        mockPageGenerator.verify();
        mockSiteTemplate.verify();
    }


    public class GroupContents extends DirectoryTemplate {

        private Location _location;


        public void generate( PageGenerator generator, SiteTemplate template, Site site ) throws IOException {
            definePage( generator, template, site, _location, "something" );
        }


        public DirectoryTemplate newFragment() {
            return new GroupContents();
        }


        public String getRootNodeName() {
            return "group-contents";
        }


        public String getFirstLocation() {
            return _location.getLocation();
        }


        public void setBase( String dir, String name ) {
            _location = new Location( "Contents", dir, "contents.html" );
        }

    }


}
