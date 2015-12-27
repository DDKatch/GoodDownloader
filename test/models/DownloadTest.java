package models;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by daniil on 27.12.15.
 */
public class DownloadTest extends TestCase {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetFolderPath() throws Exception {
        Download d = new Download();
        d.setFolderPath("");
        assertEquals("",d.getFolderPath());
    }
}