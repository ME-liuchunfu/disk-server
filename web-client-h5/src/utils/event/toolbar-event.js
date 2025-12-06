import { eventCi } from "@/utils/eventBus";

export const EVENT_KEYS = {
    TOOLBAR_CHANGE: 'toolbar:change'
}

export const tooBarEvent = {
    change: (path) => {
        if (path === '/mine') {
            tooBarEvent.mine(path)
        } else {
            tooBarEvent.home(path)
        }
    },
    home: (path) => eventCi(EVENT_KEYS.TOOLBAR_CHANGE, 'home', path),
    mine: (path) => eventCi(EVENT_KEYS.TOOLBAR_CHANGE, 'mine', path),
}
