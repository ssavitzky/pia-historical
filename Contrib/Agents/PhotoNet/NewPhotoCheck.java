import java.awt.*;
import java.applet.*;
import java.net.*;
import java.io.*;

// Variables:
// Trace - trace control
// URL - Url for image
// Interval update interval

public class NewPhotoCheck extends Applet implements Runnable
{

  boolean	boolean_Debug;			// Debugging enabled/disabled
  Thread	thread_This;			// Thread for refreshing
  int		int_Interval;			// Interval between refreshes ...
  URL CheckURL;
  AudioClip NewPhotoSound;
  boolean PhotosAvailable = false;
  boolean SoundPlayed = false;
  int appletHeight,appletWidth;


	public static void main(String args[])
	{
		System.out.println("Ricoh Silicon Valley/Marko Balabanovic/18 Aug 1998");
	}

	public void trace(String message)
	{
		if (boolean_Debug)
		{
			System.out.println(message);
		}
	}

	public void destroy()
	{
		trace("Applet.destroy() called");
		thread_This.stop();
	}

	public void start()
	{
		trace("Start called");
		thread_This.resume();
	}

	public void stop()
	{
		trace("Stop called");
		thread_This.suspend();

	}
	public void run()
	{
	  trace("run(): Thread started!");
	  while (true)
	    {
	      try
		{
		  // Put the refresh thread to sleep for the supplied interval
		  Thread.sleep(int_Interval*1000);
		  
		  trace("Checking for new photos");
		  URLConnection UC = CheckURL.openConnection();
		  
		  UC.setUseCaches(false);
		  UC.connect();

		  InputStream I = UC.getInputStream();
		  int b;
		  while (true) {
		      b = I.read();
		      if (b == -1) {
			break;
		      } else {
			trace("there is content");
			PhotosAvailable = true;
			if ((!SoundPlayed) && (NewPhotoSound != null)) {
			  NewPhotoSound.play();
			  SoundPlayed = true;
			}
			repaint();
		      }
		  }
		}
	      catch (Exception e)
		{
		  trace("Caught:"+e.toString());
		  PhotosAvailable = false;
		  SoundPlayed = false;
		  repaint();
		}
	    }
	}
  
	public void init()
	{
	  boolean_Debug = false;
	  if (getParameter("Trace") != null)
		{
		  boolean_Debug = true;
		}
	  
	  
	  trace("Init called");
	  
	  
	  appletHeight = size().height;
	  appletWidth = size().width;

	  setBackground(new Color(0,0,153));
	  repaint();
	  
	  NewPhotoSound = null;
	  try {
	    URL PhotoSoundUrl = new URL("http://cam:8888/PhotoNet/NewPhotoSound.au");
	    NewPhotoSound = getAudioClip(PhotoSoundUrl);
	  } catch (Exception e) {
	    trace("Could not load sound "+e.toString());
	  }

	  try {
	    CheckURL = new URL("http://cam:8888/PhotoNet/NewPhotoCheck.if");
	  } catch (MalformedURLException e) {
	  }

	  try
	    {
	      int_Interval = Integer.parseInt(getParameter("Interval"));
	    }
	  catch (Exception e)
	    {
	      // Format exception ... default interval 30
	      int_Interval = 30;
	    }
	  trace("Refresh interval="+int_Interval);
	  
	  
	  // Create and start the new thread initially suspended
	  thread_This = new Thread(this);
	  thread_This.suspend();
	  thread_This.start();
	  
	}
  
  public void paint(Graphics g)
    {
      trace("paint()");
      if (PhotosAvailable) {
	g.setFont(new Font("Helvetica",Font.BOLD,14));
	g.setColor(Color.white);
	g.drawString("New Photos from camera!",0,appletHeight);

      }
      
    }
  
}


