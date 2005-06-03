package com.meterware.website;
/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2003,2005, Russell Gold
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

/**
 *
 * @author <a href="mailto:russgold@meterware.com">Russell Gold</a>
 **/
public interface SiteTemplate {


    /**
     * Adds the standard page header to the specified string buffer, including any
     * common elements defined in the site configuration.
     */
    void appendPageHeader( StringBuffer sb, Site site, SiteLocation webPage );


    /**
     * Adds the standard page footer to the specified string buffer.
     */
    void appendPageFooter( StringBuffer sb, Site site, SiteLocation webPage );


    void appendMenuSpace( StringBuffer sb );


    /**
     * Appends an item to the site menu customized for the current page.
     * @param sb           a buffer into which the menu item should be written
     * @param source       the location of the page which will host the menu
     * @param title        the title to be used in the menu for this item
     * @param destination  the location of the target page
     */
    void appendMenuItem( StringBuffer sb, String source, String title, String destination );
}
