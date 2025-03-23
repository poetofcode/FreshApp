const { utils } = require('../utils');

async function launch(context) {
    console.log("Workers started");

    const workers = {};
    (await utils.requireAll('app/worker/')).forEach((name, value) => {
        workers[name] = value.create(context);
    });

    const withIntervals = [
        // [ new SerialWorker([workers.notifications, workers.pushes]), seconds(10) ]
        [ workers.grabber, minutes(7) ],
        [ workers.cleaner, hours(3) ],
    ];

    withIntervals.forEach((w) => {
        const worker = w[0];
        const interval = w[1];
        utils.setIntervalImmediately(async () => {
            try {
                await worker.doWork()
            } catch(err) {
                console.error(err);
            }
        }, interval);
    });
}

function seconds(sec) {
    return sec * 1000;
}

function minutes(min) {
    return min * seconds(60);
}

function hours(h) {
    return h * minutes(60);
}

class SerialWorker {

    constructor(children) {
        this.children = children;
    }

    async doWork() {
        for (const w of this.children) {
            await w.doWork();
        }
    }
}

exports.launch = launch;