const { utils } = require('../utils');

class FeedMiddleware {

	constructor(context, repositories) {
		this.context = context;
        this.repositories = context.repositories;
	}


    fetchFeed() { 
        return async (req, res, next) => {
            try {
                const page = req.body.page;
                const timestampFrom = req.body.timestamp;
                const sources = req.body.sources;

                console.log('Req body:');
                console.log(req.body);

                const result = await this.repositories.feed.getFeed(sources, page, timestampFrom);

                res.send(utils.wrapResult(result));
            } catch (err) {
                next(err);
            }
        }
    }

    fetchDashboard() { 
        return async (req, res, next) => {
            try {
                const result = await this.repositories.grabber.fetchDashboard();

                res.send(utils.wrapResult(result));
            } catch (err) {
                next(err);
            }
        }
    }

}

exports.create = (context) => new FeedMiddleware(context);