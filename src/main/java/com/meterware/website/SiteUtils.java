package com.meterware.website;
/********************************************************************************************************************
 * $Id$
 *
 * Copyright (c) 2005, Russell Gold
 *
 *******************************************************************************************************************/


import java.util.ArrayList;

/**
 * @author <a href="mailto:russgold@meterware.com">Russell Gold</a>
 */
public class SiteUtils {

    public static String relativeURL( String currentPage, String nextPage ) {
        if (nextPage.indexOf(':') >= 0) return nextPage;
        String[] currentPath = getRelativePath( currentPage );
        String[] nextPath = getRelativePath( nextPage );

        int baseIndex = 0;
        int maxIndex = Math.min( currentPath.length-1, nextPath.length-1 );
        while (baseIndex < maxIndex && currentPath[ baseIndex ].equals( nextPath[ baseIndex ] )) baseIndex++;

        StringBuffer sb = new StringBuffer();
        for (int i = baseIndex; i < currentPath.length-1; i++) sb.append( "../" );
        for (int i = baseIndex; i < nextPath.length-1; i++) sb.append( nextPath[i] ).append( '/' );
        sb.append( nextPath[ nextPath.length-1] );
        return sb.toString();
    }


    static String[] getRelativePath( String url ) {
        ArrayList parts = new ArrayList();
        int start = 0;
        int end;
        do {
            end = url.indexOf( '/', start );
            if (end >= 0) {
                parts.add( url.substring( start, end ));
                start = end+1;
            }
        } while (end >= 0);
        parts.add( url.substring( start ) );
        return (String[]) parts.toArray( new String[ parts.size() ]);
    }
}
