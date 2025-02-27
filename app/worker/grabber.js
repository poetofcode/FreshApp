const ObjectId = require("mongodb").ObjectId;
const { utils } = require('../utils');

class GrabberWorker {

	constructor(context) {
		this.context = context;
	}

    async doWork() {
	    console.log("Работает grabber (worker)");

	    /*
			Тут будет вызываться GrabRepository
			Что то наподобие:

			const feed = async grabberRepository.fetchTotalFeed();
			
			Далее сохраняем это:

			async feedRepository.saveFeed(feed);

			Если ошибка, то логгируем её пока что просто
	    */
    }

}

exports.create = (context) => new GrabberWorker(context); 