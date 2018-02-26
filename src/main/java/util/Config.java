package util;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Config
{
    private Properties prop = new Properties();

    private String cfgName;

    private Scanner scanner;

    private File cfg;

    private FileOutputStream output;

    private FileInputStream input;

    public Config( String cfgName )
    {
        this.cfgName = cfgName;

        cfg = new File( cfgName );

        if( !cfg.exists() )
        {
            try
            {
                cfg.createNewFile();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        scanner = new Scanner( System.in );
    }

    public Config( String dirStr, String cfgName )
    {
        this.cfgName = cfgName;

        File dir = new File( dirStr );

        if( !dir.exists() )
        {
            dir.mkdirs();
        }

        cfg = new File( dirStr + cfgName + "/");
    }

    public String getToken()
    {
        String returnValue, token = "LoginToken";

        read( cfg, prop );

        returnValue = prop.getProperty( token );

        if( returnValue == null )
        {
            System.out.print("Login token not set please input login token: ");

            returnValue = scanner.nextLine();

            input = read( cfg, prop );

            //incase file already created
            finishRead( input );

            try
            {
                output = new FileOutputStream( cfg );
            }
            catch ( FileNotFoundException e )
            {
                e.printStackTrace();
            }

            prop.setProperty( token, returnValue );

            finishWrite( output, prop );
        }

        return returnValue;
    }

    public String getOwner()
    {
        String returnValue, owner = "owner";

        read( cfg, prop );

        returnValue = prop.getProperty( owner );

        if( returnValue == null )
        {
            System.out.print("bot owner not set please input bot owner ID: ");

            returnValue = scanner.nextLine();

            input = read( cfg, prop );

            //incase file already created
            finishRead( input );

            try
            {
                output = new FileOutputStream( cfg );
            }
            catch ( FileNotFoundException e )
            {
                e.printStackTrace();
            }

            prop.setProperty( owner, returnValue );

            finishWrite( output, prop );
        }

        return returnValue;
    }


    public String getProp( String propertyName )
    {
        input = read( cfg, prop );

        String value = prop.getProperty( propertyName.toLowerCase() );

        finishRead( input );

        return value;
    }

    public void setProp ( String propertyName, String value )
    {
        FileOutputStream output;

        try
        {
            FileInputStream input = new FileInputStream( cfg );
        }
        catch ( FileNotFoundException e )
        {
            return;
        }

        if( cfg.exists() )
        {
            input = read( cfg, prop );

            finishRead( input );

            output = write( cfg );
        }
        else
        {
            output = write( cfg );
        }

        prop.setProperty( propertyName.toLowerCase(), value );

        finishWrite( output, prop );
    }

    //opens new input stream
    private FileInputStream read( File file, Properties prop )
    {
        FileInputStream input;
        //trys to load file
        try
        {
            input = new FileInputStream(file);

            prop.load(input);
        }
        catch (IOException e)
        {
            //if file does not exists, creates it and recursively call read
            fileCreate(file);

            input = read(file, prop);
        }

        return input;
    }


    //closes input stream
    private void finishRead( InputStream input ){
        try
        {
            input.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //opens output steam
    private FileOutputStream write( File file )
    {
        FileOutputStream output = null;

        try
        {
            output = new FileOutputStream(file);
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        return output;
    }


    //close output stream
    private void finishWrite( FileOutputStream output, Properties prop ){
        try
        {
            prop.store(output, null);
            output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    private void fileCreate( File file )
    {
        //creates file, used in read in case file does not exist
        boolean isCreated = false;

        try
        {
            isCreated = file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if( isCreated )
        {
            System.out.println(file.getName() + " created successfully");
        }
    }

}