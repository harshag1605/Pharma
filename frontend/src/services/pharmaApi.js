import { createApi, fetchBaseQuery, retry } from '@reduxjs/toolkit/query/react';
import { logout, setCredentials } from '../features/auth/authSlice';

const base = fetchBaseQuery({
  baseUrl: import.meta.env.VITE_API_URL || '',
  prepareHeaders: (headers, ctx) => {
    const token = ctx.getState().auth.accessToken;

    if (token) {
      headers.set('authorization', `Bearer ${token}`);
    }

    headers.set('x-correlation-id', crypto.randomUUID());
    return headers;
  }
});

const baseQueryWithRefresh = async (args, api, extraOptions) => {
  const run = retry(base, { maxRetries: 2 });

  let result = await run(args, api, extraOptions);

  const state = api.getState().auth;

  if (result?.error?.status === 401 && state.refreshToken) {
    const refreshResponse = await base(
      {
        url: '/api/auth/refresh',
        method: 'POST',
        body: { refreshToken: state.refreshToken }
      },
      api,
      extraOptions
    );

    const newTokens = refreshResponse?.data?.data;

    if (newTokens) {
      api.dispatch(setCredentials(newTokens));
      result = await base(args, api, extraOptions);
    } else {
      api.dispatch(logout());
    }
  }

  return result;
};

export const pharmaApi = createApi({
  reducerPath: 'pharmaApi',
  baseQuery: baseQueryWithRefresh,

  tagTypes: [
    'Catalog',
    'Orders',
    'Prescriptions',
    'Inventory',
    'Deliveries',
    'Users'
  ],

  endpoints: (builder) => {

    const post = (url) => (body) => ({
      url,
      method: 'POST',
      body
    });

    const patch = (url) => (body) => ({
      url,
      method: 'PATCH',
      body
    });

    return {

      login: builder.mutation({
        query: post('/api/auth/login')
      }),

      register: builder.mutation({
        query: post('/api/auth/register')
      }),

      searchMedicines: builder.query({
        query: (q = '') =>
          `/api/catalog/medicines?q=${encodeURIComponent(q)}&size=25`,
        providesTags: ['Catalog']
      }),

      substitutes: builder.query({
        query: (id) =>
          `/api/catalog/medicines/${id}/substitutes`
      }),

      uploadPrescription: builder.mutation({
        query: (file) => {
          const form = new FormData();
          form.append('file', file);

          return {
            url: '/api/prescriptions',
            method: 'POST',
            body: form
          };
        },
        invalidatesTags: ['Prescriptions']
      }),

      myPrescriptions: builder.query({
        query: () => '/api/prescriptions/mine',
        providesTags: ['Prescriptions']
      }),

      signedPrescriptionUrl: builder.query({
        query: (id) =>
          `/api/prescriptions/${id}/signed-url`
      }),

      createOrder: builder.mutation({
        query: post('/api/orders'),
        invalidatesTags: ['Orders', 'Inventory']
      }),

      createPrescriptionRequest: builder.mutation({
        query: post('/api/orders/prescription-request'),
        invalidatesTags: ['Orders']
      }),

      myOrders: builder.query({
        query: () =>
          '/api/orders/mine?size=20&sort=createdAt,desc',
        providesTags: ['Orders']
      }),

      allOrders: builder.query({
        query: () =>
          '/api/orders?size=50&sort=createdAt,desc',
        providesTags: ['Orders']
      }),

      updateOrderStatus: builder.mutation({
        query: ({ id, ...body }) => ({
          url: `/api/orders/${id}/status`,
          method: 'PATCH',
          body
        }),
        invalidatesTags: ['Orders']
      }),

      setOrderItems: builder.mutation({
        query: ({ id, items }) => ({
          url: `/api/orders/${id}/items`,
          method: 'PUT',
          body: { items }
        }),
        invalidatesTags: ['Orders', 'Inventory']
      }),

      inventory: builder.query({
        query: () => '/api/inventory',
        providesTags: ['Inventory']
      }),

      upsertInventory: builder.mutation({
        query: post('/api/inventory'),
        invalidatesTags: ['Inventory', 'Catalog']
      }),

      users: builder.query({
        query: (role) =>
          `/api/users${role ? `?role=${role}` : ''}`,
        providesTags: ['Users']
      }),

      assignDelivery: builder.mutation({
        query: ({ orderId, agentId }) => ({
          url: `/api/deliveries/orders/${orderId}/assign`,
          method: 'POST',
          body: { agentId }
        }),
        invalidatesTags: ['Deliveries', 'Orders']
      }),

      myDeliveries: builder.query({
        query: () => '/api/deliveries/mine',
        providesTags: ['Deliveries']
      }),

      pickedUp: builder.mutation({
        query: (id) => ({
          url: `/api/deliveries/${id}/picked-up`,
          method: 'PATCH'
        }),
        invalidatesTags: ['Deliveries', 'Orders']
      }),

      confirmDelivery: builder.mutation({
        query: ({ id, otp }) => ({
          url: `/api/deliveries/${id}/confirm`,
          method: 'PATCH',
          body: { otp }
        }),
        invalidatesTags: ['Deliveries', 'Orders']
      })
    };
  }
});

export const {
  useLoginMutation,
  useRegisterMutation,
  useSearchMedicinesQuery,
  useSubstitutesQuery,
  useUploadPrescriptionMutation,
  useMyPrescriptionsQuery,
  useCreateOrderMutation,
  useCreatePrescriptionRequestMutation,
  useMyOrdersQuery,
  useAllOrdersQuery,
  useUpdateOrderStatusMutation,
  useSetOrderItemsMutation,
  useInventoryQuery,
  useUpsertInventoryMutation,
  useUsersQuery,
  useAssignDeliveryMutation,
  useMyDeliveriesQuery,
  usePickedUpMutation,
  useConfirmDeliveryMutation
} = pharmaApi;