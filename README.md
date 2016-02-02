# GLChartDrawer
Use android opengl draw chart(point, lines, text and so on), not run in main-UI thread.


Usage:
Activity -> onCreate():

  private SampleSurfaceView mSampleSrufaceView;
  
	protected void onCreate(Bundle savedInstanceState) {
		SampleDrawer drawer = new SampleDrawer(this);
		mSampleSrufaceView = new SampleSurfaceView(this, drawer);
		setContentView(mSampleSrufaceView);
		TimerTask tk = new TimerTask() {
		long runTimes = 0;
					
		@Override
		public void run() {
			if (null != mSampleSrufaceView) 
				mSampleSrufaceView.updateRunTimes(runTimes++);// update drawer
			}
		};
		Timer timer = new Timer(true);
		timer.schedule(tk, 1000, 500);
	}
