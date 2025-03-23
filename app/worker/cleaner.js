class CleanerWorker {

	constructor(context) {
		this.context = context;
		this.repositories = context.repositories;
	}

    async doWork() {
	    console.log("Работает cleaner (worker)");
    	await this.repositories.feed.cleanOldRecords();
    }

}

exports.create = (context) => new CleanerWorker(context); 