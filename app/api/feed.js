const { utils } = require('../utils');

class FeedMiddleware {

	constructor(context, repositories) {
		this.context = context;
        this.repositories = context.repositories;
	}


    fetchFeed() { 
        return async (req, res, next) => {
            try {
                const page = '0';   // TODO pare page from post-parameters
                const sources = ['dtf', 'habr'];    // TODO same
                const timestampFrom = null;         // TODO same

                const result = await this.repositories.feed.getFeed(sources, page, timestampFrom);

                res.send(utils.wrapResult(result));
            } catch (err) {
                next(err);
            }
        }
    }

}

exports.create = (context) => new FeedMiddleware(context);