declare interface Window {
    Quanmiao: any;
    Duix: any;
}

declare module 'duix-guiji-light' {
    export default class Duix {
        constructor();
        init(options: any): void;
        start(options: any): Promise<any>;
        on(event: string, callback: any): void;
    }
}