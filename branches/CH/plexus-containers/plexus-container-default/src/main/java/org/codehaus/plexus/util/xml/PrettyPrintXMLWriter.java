package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;

public class PrettyPrintXMLWriter implements XMLWriter
{

    private PrintWriter writer;
    private LinkedList elementStack = new LinkedList();
    private boolean tagInProgress;
    private int depth;
    private String lineIndenter;
    private boolean readyForNewLine;
    private boolean tagIsEmpty;

    public PrettyPrintXMLWriter( PrintWriter writer, String lineIndenter )
    {
        this.writer = writer;
        this.lineIndenter = lineIndenter;
    }

    public PrettyPrintXMLWriter( Writer writer, String lineIndenter )
    {
        this( new PrintWriter( writer ), lineIndenter );
    }

    public PrettyPrintXMLWriter( PrintWriter writer )
    {
        this( writer, "  " );
    }

    public PrettyPrintXMLWriter( Writer writer )
    {
        this( new PrintWriter( writer ) );
    }

    public void startElement( String name )
    {
        tagIsEmpty = false;
        finishTag();
        write( "<" );
        write( name );
        elementStack.addLast( name );
        tagInProgress = true;
        depth++;
        readyForNewLine = true;
        tagIsEmpty = true;
    }

    public void writeText( String text )
    {
        readyForNewLine = false;
        tagIsEmpty = false;
        finishTag();
        text = text.replaceAll( "&", "&amp;" );
        text = text.replaceAll( "<", "&lt;" );
        text = text.replaceAll( ">", "&gt;" );
        write( text );
    }

    public void addAttribute( String key, String value )
    {
        write( " " );
        write( key );
        write( "=\"" );
        write( value );
        write( "\"" );
    }

    public void endElement()
    {
        depth--;
        if ( tagIsEmpty )
        {
            write( "/" );
            readyForNewLine = false;
            finishTag();
            elementStack.removeLast();
        }
        else
        {
            finishTag();
            write( "</" + elementStack.removeLast() + ">" );
        }
        readyForNewLine = true;
    }

    private void write( String str )
    {
        writer.write( str );
    }

    private void finishTag()
    {
        if ( tagInProgress )
        {
            write( ">" );
        }
        tagInProgress = false;
        if ( readyForNewLine )
        {
            endOfLine();
        }
        readyForNewLine = false;
        tagIsEmpty = false;
    }

    protected void endOfLine()
    {
        write( "\n" );
        for ( int i = 0; i < depth; i++ )
        {
            write( lineIndenter );
        }
    }
}
