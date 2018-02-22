import java.io.*;
import java.util.Properties;
import java.util.Scanner;

public class Config
{
    private Properties prop = new Properties();

    private String cfgName;

    private Scanner scanner;

    private File cfg;

    private OutputStream output;

    private InputStream input;

    Config( String cfgName )
    {
        this.cfgName = cfgName;

        cfg = new File( cfgName );

        scanner = new Scanner( System.in );
    }

    public String getToken()
    {
        String returnValue, token = "LoginToken";

        read();

        returnValue = prop.getProperty( token );

        if( returnValue == null )
        {
            System.out.print("Login token not set please input login token: ");

            returnValue = scanner.nextLine();

            //incase file already created
            finishRead();

            try
            {
                output = new FileOutputStream( cfg );
            }
            catch ( FileNotFoundException e )
            {
                e.printStackTrace();
            }

            prop.setProperty( token, returnValue );
            finishWrite();
        }

        return returnValue;
    }

    public String getPrefix( String guildID )
    {
        String returnValue;

        read();

        returnValue = prop.getProperty( guildID );

        if( returnValue == null )
        {
            //incase file already created
            finishRead();

            try
            {
                output = new FileOutputStream( cfg );
            }
            catch ( FileNotFoundException e )
            {
                e.printStackTrace();
            }

            prop.setProperty( guildID, "?" );
            finishWrite();

            return "NEWSERVER";
        }

        return returnValue;
    }

    private void read()
    {
        try
        {
            input = new FileInputStream( cfg );

            prop.load( input );

        }
        catch (IOException e)
        {
            fileCreate( cfg );
            read();
        }
    }


    private void finishWrite(){
        try {
            prop.store(output, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finishRead(){
        try {
            input.close();
        }catch (Exception e){
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