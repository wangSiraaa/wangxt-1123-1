import request from '@/utils/request';
import type {
  FodEvent,
  EventReportDTO,
  EventEvaluateDTO,
  EventHandleDTO,
  EventCloseDTO,
  PageQuery,
  PageResult,
  Result,
} from '@/types';

export const eventApi = {
  report: (data: EventReportDTO): Promise<Result<number>> => {
    return request.post('/event/report', data);
  },

  getDetail: (id: number): Promise<Result<FodEvent>> => {
    return request.get(`/event/${id}`);
  },

  getPage: (params: PageQuery & Record<string, any>): Promise<Result<PageResult<FodEvent>>> => {
    const { pageNum, pageSize, ...rest } = params;
    return request.post('/event/page', { pageNum, pageSize }, { params: rest });
  },

  getList: (params?: Record<string, any>): Promise<Result<FodEvent[]>> => {
    return request.get('/event/list', { params });
  },

  evaluate: (data: EventEvaluateDTO): Promise<Result<FodEvent>> => {
    return request.post('/event/evaluate', data);
  },

  startHandle: (eventId: number, handlerId?: string, handlerName?: string): Promise<Result<FodEvent>> => {
    return request.post(`/event/handle/start/${eventId}`, null, {
      params: { handlerId, handlerName },
    });
  },

  completeHandle: (data: EventHandleDTO): Promise<Result<FodEvent>> => {
    return request.post('/event/handle/complete', data);
  },

  close: (data: EventCloseDTO): Promise<Result<FodEvent>> => {
    return request.post('/event/close', data);
  },

  cancel: (eventId: number, operatorId?: string, operatorName?: string): Promise<Result<FodEvent>> => {
    return request.post(`/event/cancel/${eventId}`, null, {
      params: { operatorId, operatorName },
    });
  },

  updateRiskLevel: (
    eventId: number,
    riskLevel: number,
    operatorId?: string,
    operatorName?: string
  ): Promise<Result<FodEvent>> => {
    return request.post('/event/risk-level', null, {
      params: { eventId, riskLevel, operatorId, operatorName },
    });
  },

  getStatistics: (): Promise<Result<Record<string, number>>> => {
    return request.get('/event/statistics');
  },
};
