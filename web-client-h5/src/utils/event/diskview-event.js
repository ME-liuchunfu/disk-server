import eventBus from "@/utils/eventBus";

const event = (key, type, value) => {
    eventBus.emit(key, {
        type: type,
        value: value
    })
}


export const diskViewEvent = {
    renameDir: (value) => event('event:disk:rename-dir','rename', value),
    updateAvatar: (value) => event('event:disk:add-avatar', 'avatar', value),
    addFolder: (value) => event('event:disk:add-folder', 'addfolder', value),
    spider: (value) => event('event:disk:spider', 'spider', value),
}
