const ObjectId = require("mongodb").ObjectId;
const crypto = require('crypto');
const { utils } = require('../utils');


class FeedRepository {

	constructor(context) {
		this.context = context;
	}

	async saveFeed(feed) {

        return [];
	}

}

exports.create = (context) => new FeedRepository(context);