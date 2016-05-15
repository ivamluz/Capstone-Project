package ivamluz.marvelshelf;

import com.karumi.marvelapiclient.model.MarvelImage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ivamluz.marvelshelf.helpers.MarvelImageHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarvelImageHelperUnitTest {
    private static final String IMAGE_PATH = "http://i.annihil.us/u/prod/marvel/i/mg/3/10/5130f81a682b5";
    private static final String IMAGE_EXTENSION = "jpg";
    private static final String IMAGE_URL = String.format("%s.%s", IMAGE_PATH, IMAGE_EXTENSION);

    @Mock
    MarvelImage mMarvelImage;

    @Test
    public void shouldCreateCorrectImageUrl() throws Exception {
        String resizedUrl = MarvelImageHelper.buildSizedImageUrl(IMAGE_URL, MarvelImage.Size.LANDSCAPE_SMALL);

        String expectedUrl = "http://i.annihil.us/u/prod/marvel/i/mg/3/10/5130f81a682b5/landscape_small.jpg";
        assertEquals(expectedUrl, resizedUrl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotChangeUrlIfDoesntMatchPattern() {
        MarvelImageHelper.buildSizedImageUrl(IMAGE_URL, null);
    }

    @Test
    public void shouldReturnUrlFromMarvelImageObject() {
        when(mMarvelImage.getPath()).thenReturn(IMAGE_PATH);
        when(mMarvelImage.getExtension()).thenReturn(IMAGE_EXTENSION);

        String imageUrl = MarvelImageHelper.getUrlFromImage(mMarvelImage);
        assertEquals(IMAGE_URL, imageUrl);
    }

    @Test
    public void shouldReturnNullUrlIfMarvelImageObjectIsNull() {
        assertNull(MarvelImageHelper.getUrlFromImage(null));
    }
}
