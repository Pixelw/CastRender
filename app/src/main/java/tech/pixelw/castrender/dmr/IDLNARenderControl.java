package tech.pixelw.castrender.dmr;

import android.widget.VideoView;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 *
 */
public interface IDLNARenderControl {
    void play();

    void pause();

    void seek(long position);

    void stop();

    long getPosition();

    long getDuration();

    // -------------------------------------------------------------------------------------------
    // - VideoView impl
    // -------------------------------------------------------------------------------------------
    final class VideoViewRenderControl implements IDLNARenderControl {

        private final VideoView videoView;

        public VideoViewRenderControl(VideoView videoView) {
            this.videoView = videoView;
        }

        @Override
        public void play() {
            videoView.start();
        }

        @Override
        public void pause() {
            videoView.pause();
        }

        @Override
        public void seek(long position) {
            videoView.seekTo((int) position);
        }

        @Override
        public void stop() {
            videoView.stopPlayback();
        }

        @Override
        public long getPosition() {
            return videoView.getCurrentPosition();
        }

        @Override
        public long getDuration() {
            return videoView.getDuration();
        }
    }

    // -------------------------------------------------------------------------------------------
    // - Default impl
    // -------------------------------------------------------------------------------------------
    final class DefaultRenderControl implements IDLNARenderControl {
        public DefaultRenderControl() {
        }

        @Override
        public void play() {
        }

        @Override
        public void pause() {
        }

        @Override
        public void seek(long position) {
        }

        @Override
        public void stop() {
        }

        @Override
        public long getPosition() {
            return 0L;
        }

        @Override
        public long getDuration() {
            return 0L;
        }
    }

    /**
     * Exoplayer Impl
     */
    final class ExoPlayerRenderControl implements IDLNARenderControl{
        private final SimpleExoPlayer player;
        public ExoPlayerRenderControl(SimpleExoPlayer player) {
            this.player = player;
        }

        @Override
        public void play() {
            player.play();
        }

        @Override
        public void pause() {
            player.pause();
        }

        @Override
        public void seek(long position) {
            player.seekTo(position);
        }

        @Override
        public void stop() {
            player.stop();
        }

        @Override
        public long getPosition() {
            return player.getCurrentPosition();
        }

        @Override
        public long getDuration() {
            return player.getDuration();
        }
    }
}
