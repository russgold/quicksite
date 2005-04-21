package com.meterware.website;
/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2004, Russell Gold
 *
 *******************************************************************************************************************/
import junit.framework.TestSuite;

import java.io.File;

/**
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
public class PageFragmentTest extends QuickSiteTestCase {


    public static void main( String[] args ) {
        junit.textui.TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        return new TestSuite( PageFragmentTest.class );
    }



    /**
     * Verifies that the contents of a simple text file are returned as the contents of a fragment.
     */
    public void testSimpleTextFragment() throws Exception {
        String CONTENTS = "A simple line of text";
        File file = createTextFile( CONTENTS );

        PageFragment fragment = new PageFragment();
        fragment.setSource( file.getAbsolutePath() );
        assertEquals( "Fragment contents", CONTENTS, fragment.asText() );
    }



    /**
     * Verifies that the contents of a simple text file are returned as the contents of a fragment.
     */
    public void testTextFragmentParagraphs() throws Exception {
        File file = createTextFile( "A line of text\r\nA second line" );

        PageFragment fragment = new PageFragment();
        fragment.setSource( file.getAbsolutePath() );
        assertEquals( "Fragment contents", "A line of text\r\n<p>A second line", fragment.asText() );
    }



    /**
     * Verifies substitution of common non-Latin1 characters: rsquo, rdquo, ldquo, lsquo, and mdash.
     */
    public void testSubstituteEntities1() throws Exception {
        File file = createTextFile( "A \u201cline of text\u201d \u2014 A \u2018second\u2019 line" );

        PageFragment fragment = new PageFragment();
        fragment.setSource( file.getAbsolutePath() );
        assertEquals( "Fragment contents", "A &ldquo;line of text&rdquo; &mdash; A &lsquo;second&rsquo; line", fragment.asText() );
    }


}
