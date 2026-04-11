export {};

declare global {
  const google: {
    accounts: {
      oauth2: {
        initCodeClient(config: {
          client_id: string;
          scope: string;
          ux_mode?: 'popup' | 'redirect';
          callback: (response: { code: string; error?: string }) => void;
        }): { requestCode(): void };
      };
      id: {
        initialize(config: {
          client_id: string;
          callback: (response: { credential: string }) => void;
          auto_select?: boolean;
          cancel_on_tap_outside?: boolean;
        }): void;
        renderButton(
          parent: HTMLElement,
          options: {
            theme?: 'outline' | 'filled_blue' | 'filled_black';
            size?: 'large' | 'medium' | 'small';
            width?: number | string;
            text?: 'signin_with' | 'signup_with' | 'continue_with' | 'signin';
            locale?: string;
            type?: 'standard' | 'icon';
          }
        ): void;
        disableAutoSelect(): void;
      };
    };
  };
}
