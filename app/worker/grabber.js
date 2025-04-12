class GrabberWorker {

	constructor(context) {
		this.context = context;
		this.repositories = context.repositories;
	}

    async doWork() {
	    console.log("Работает grabber (worker)");
    	const feed = await this.repositories.grabber.fetchTotalFeed();		
		await this.repositories.feed.saveFeed(feed);
    }

}

exports.create = (context) => new GrabberWorker(context); 