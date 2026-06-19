import request from '@/utils/request';
import type { Runway, FodEventLog, Result } from '@/types';

export { eventApi } from './event';
export { photoApi } from './photo';
export { clearanceApi } from './clearance';

export const runwayApi = {
  getAll: (): Promise<Result<Runway[]>> => {
    return request.get('/runway/list');
  },

  getById: (id: number): Promise<Result<Runway>> => {
    return request.get(`/runway/${id}`);
  },

  getByCode: (code: string): Promise<Result<Runway>> => {
    return request.get(`/runway/code/${code}`);
  },
};

export const eventLogApi = {
  getByEventId: (eventId: number): Promise<Result<FodEventLog[]>> => {
    return request.get(`/event-log/event/${eventId}`);
  },
};
