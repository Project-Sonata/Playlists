package testing.spring;

import com.odeyalo.sonata.playlists.support.converter.ImageEntityConverterImpl;
import com.odeyalo.sonata.playlists.support.converter.ImagesEntityConverterImpl;
import com.odeyalo.sonata.playlists.support.converter.PlaylistConverterImpl;
import com.odeyalo.sonata.playlists.support.converter.PlaylistOwnerConverterImpl;
import org.springframework.context.annotation.Import;

@Import({
        ImageEntityConverterImpl.class,
        ImagesEntityConverterImpl.class,
        PlaylistConverterImpl.class,
        PlaylistOwnerConverterImpl.class
})
public class ConvertersConfiguration {
}
